package xyz.nietongxue.common.json

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

fun jo(fu: ObjectNode.() -> Unit = {}): ObjectNode {
    return jacksonObjectMapper().createObjectNode().apply {
        fu()
    }
}

fun ja(vararg nodes: JsonNode): ArrayNode {
    return jacksonObjectMapper().createArrayNode().apply {
        nodes.forEach {
            add(it)
        }
    }
}