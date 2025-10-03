package xyz.nietongxue.common.base


import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong


interface SnowflakeConfig {
    fun getCurrentDataCenterIn5Bit(): Long
    // Return your datacenter ID (0 to 31)


    fun getCurrentInstanceIndex(): Long
    // Return your machine ID (0 to 31)

}

class SnowflakeIdGenerator(eurekaConfig: SnowflakeConfig) {
    private val datacenterId: Long = eurekaConfig.getCurrentDataCenterIn5Bit()
    private val machineId: Long = eurekaConfig.getCurrentInstanceIndex()
    private val sequence = AtomicLong(0L)
    private val lastTimestamp = AtomicLong(-1L)
    private val threadWaitCount = ConcurrentHashMap<Long, AtomicLong>()

    init {
        require(!(datacenterId > MAX_DATACENTER_ID || datacenterId < 0)) { "Datacenter ID can't be greater than $MAX_DATACENTER_ID or less than 0" }
        require(!(machineId > MAX_MACHINE_ID || machineId < 0)) { "Machine ID can't be greater than $MAX_MACHINE_ID or less than 0" }
    }

    @Synchronized
    fun generateId(): Long {
        var timestamp = System.currentTimeMillis()
        val lastTs = lastTimestamp.get()
        if (timestamp < lastTs) {
            throw RuntimeException("Clock moved backwards. Refusing to generate id")
        }
        if (timestamp == lastTs) {
            val seq = sequence.incrementAndGet() and SEQUENCE_MASK
            if (seq == 0L) {
                timestamp = waitForNextMillis(lastTs)
            }
        } else {
            sequence.set(0L)
        }
        lastTimestamp.set(timestamp)
        return (((timestamp - EPOCH) shl (DATACENTER_ID_BITS + MACHINE_ID_BITS + SEQUENCE_BITS).toInt())
                or (datacenterId shl (MACHINE_ID_BITS + SEQUENCE_BITS).toInt())
                or (machineId shl SEQUENCE_BITS.toInt())
                or sequence.get())
    }


    private fun waitForNextMillis(lastTimestamp: Long): Long {
        var timestamp = System.currentTimeMillis()
        val waitCount = threadWaitCount.computeIfAbsent(
            lastTimestamp
        ) { k: Long? ->
            AtomicLong(
                0
            )
        }.incrementAndGet()
        while (timestamp <= lastTimestamp) {
            try {
                Thread.sleep(waitCount) // Wait for existing thread wait count + 1 millisecond
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt() // Restore interrupt status
                throw RuntimeException("Interrupted while waiting for next millisecond", e)
            }
            timestamp = System.currentTimeMillis()
        }
        threadWaitCount.remove(lastTimestamp) // Clean up the wait count for the past timestamp
        return timestamp
    }

    companion object {
        private const val EPOCH = 1704067200000L // Company's epoch = Monday, January 1, 2024 12:00:00 AM
        private const val DATACENTER_ID_BITS = 5L
        private const val MACHINE_ID_BITS = 5L
        private const val SEQUENCE_BITS = 12L
        private const val MAX_DATACENTER_ID = -1L xor (-1L shl DATACENTER_ID_BITS.toInt())
        private const val MAX_MACHINE_ID = -1L xor (-1L shl MACHINE_ID_BITS.toInt())
        private const val SEQUENCE_MASK = -1L xor (-1L shl SEQUENCE_BITS.toInt())
    }
}
