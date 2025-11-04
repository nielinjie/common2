package xyz.nietongxue.common.json

import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.slf4j.LoggerFactory
import xyz.nietongxue.common.testing.pretty
import kotlin.test.Test

class JqTest {
    val om = jacksonObjectMapper()
    val logger = LoggerFactory.getLogger(JqTest::class.java)

    @Test
    fun test() {
        val data = mapOf("a" to 1, "b" to 2)
        val transfer = """
            .a
             """
        val transferType = JQ_TRANSFER_FIRST
        val result = transform(om.valueToTree<ObjectNode>(data), transfer, transferType)
        logger.pretty(null, result)
    }
    @Test
    fun test2() {
        val data = mapOf("a" to 1, "b" to 2)
        val transfer = """
            .a
             """
        val transferType = JQ_TRANSFER_FIRST
        val result = data.transform( transfer, JQ_TRANSFER_FIRST)
        logger.pretty( result)
    }

}