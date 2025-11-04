package xyz.nietongxue.common.taskdag

import org.junit.jupiter.api.Test
import xyz.nietongxue.common.taskdag.stringEvent.CommonEvents.EXCEPTION
import xyz.nietongxue.common.taskdag.stringEvent.CommonEvents.START
import xyz.nietongxue.common.taskdag.stringEvent.CommonEvents.SUCCESS
import xyz.nietongxue.common.taskdag.stringEvent.CommonNodes.DONE
import xyz.nietongxue.common.taskdag.stringEvent.CommonNodes.INIT
import xyz.nietongxue.common.taskdag.stringEvent.defaultCatching
import xyz.nietongxue.common.taskdag.stringEvent.retry

class RetryTest() {
    @Test
    fun retryTest() {
        val dag = dag {
            init()
            end()
            retry(mock("t1", EXCEPTION, SUCCESS), 3)
            mock("t2", SUCCESS)
            INIT.to("t1").on(START)
            "t1".on(SUCCESS).to("t2")
            "t2".on(SUCCESS).to(DONE)
        }
        dag.start(START).also {
            it.waitForEnd()
        }
    }

    @Test
    fun retryTest2() {
        val dag = dag {
            init()
            end()
            retry(mock("t1", EXCEPTION, EXCEPTION, EXCEPTION, EXCEPTION), 3)
            mock("t2", SUCCESS)
            INIT.to("t1").on(START)
            "t1".on(SUCCESS).to("t2")
            "t2".on(SUCCESS).to(DONE)
            defaultCatching()
        }
        dag.start(START).also {
            it.waitForEnd()
        }
    }

    @Test
    fun retryTest3() {
        val dag = dag {
            init()
            end()
            retry(mock("t1", EXCEPTION, EXCEPTION, SUCCESS), 3)
            mock("t2", EXCEPTION)
            INIT.to("t1").on(START)
            "t1".on(SUCCESS).to("t2")
            "t2".on(SUCCESS).to(DONE)
            defaultCatching()
        }
        dag.start(START).also {
            it.waitForEnd()
        }
    }

    @Test
    fun retryTest4() {
        val dag = dag {
            init()
            end()
            retry(mock("t1", EXCEPTION, EXCEPTION, SUCCESS + "2"), 3)
            mock("t2", EXCEPTION)
            mock("t3", SUCCESS)
            INIT.to("t1").on(START)
            "t1".on(SUCCESS).to("t2")
            "t1".on(SUCCESS + "2").to("t3")
            "t2".on(SUCCESS).to(DONE)
            "t3".on(SUCCESS).to(DONE)
            defaultCatching()
        }
        dag.start(START).also {
            it.waitForEnd()
        }
    }
}