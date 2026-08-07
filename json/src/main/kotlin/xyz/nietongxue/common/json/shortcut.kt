package xyz.nietongxue.common.json

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import org.slf4j.Logger

/**
 * 一些 json 的快捷操作。
 */


fun autoJson(any: Any?, logger: Logger? = null): JsonNode? {
    return when (any) {
        null -> null
        is ObjectNode -> any
        is String -> try {
            defaultOM.readTree(any)
        } catch (e: Exception) {
            logger?.warn("autoJson as String error: $any")
            logger?.warn("autoJson as String error: ${e.stackTraceToString()}")
            null
        }

        is List<*> -> any.mapNotNull { autoJson(it, logger) }.let { ja(*it.toTypedArray()) }
        is Map<*, *> -> any.toList().mapNotNull {
            (it.first as? String)?.let { key -> autoJson(it.second, logger)?.let { key to it } }
        }.let {
            jo(*it.toTypedArray())
        }

        else -> try {
            defaultOM.valueToTree(any)
        } catch (e: Exception) {
            logger?.warn("autoJson error: $any")
            logger?.warn("autoJson error: ${e.stackTraceToString()}")
            null
        }
    }
}

fun autoJsonO(any: Any?, logger: Logger? = null): ObjectNode? {
    return autoJson(any, logger) as? ObjectNode
}

fun autoJsonA(any: Any?, logger: Logger? = null): ArrayNode? {
    return autoJson(any, logger) as? ArrayNode
}