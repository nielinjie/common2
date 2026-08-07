package xyz.nietongxue.common.json

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.IntNode
import com.fasterxml.jackson.databind.node.LongNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import xyz.nietongxue.common.json.defaultOM


fun String.asJson(): JsonNode {
    return TextNode(this)
}

fun Int.asJson(): JsonNode {
    return IntNode(this)
}

fun Long.asJson(): JsonNode {
    return LongNode(this)
}

fun jo(fu: ObjectNode.() -> Unit = {}): ObjectNode {
    return defaultOM.createObjectNode().apply {
        fu()
    }
}

fun jo(vararg pairs: Pair<String, JsonNode>): ObjectNode {
    return jo {
        pairs.forEach {
            put(it.first, it.second)
        }
    }
}

fun jo(pairs: Map<String, JsonNode>): ObjectNode {
    return jo(*pairs.toList().toTypedArray())
}

fun ja(vararg nodes: JsonNode): ArrayNode {
    return defaultOM.createArrayNode().apply {
        nodes.forEach {
            add(it)
        }
    }
}