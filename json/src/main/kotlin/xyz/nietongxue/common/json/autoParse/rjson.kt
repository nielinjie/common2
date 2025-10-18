package xyz.nietongxue.common.json.autoParse

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.BooleanNode
import com.fasterxml.jackson.databind.node.BooleanNode.TRUE
import com.fasterxml.jackson.databind.node.DoubleNode
import com.fasterxml.jackson.databind.node.NullNode
import com.fasterxml.jackson.databind.node.TextNode
import tv.twelvetone.json.Json
import tv.twelvetone.json.JsonValue

object RJson {
    fun parse(json: String): JsonValue {
        return Json.parse(json)
    }

    fun rjsonToJackson(jsonValue: JsonValue): JsonNode {
        val om = ObjectMapper()
        return when {
            jsonValue.isString -> {
                TextNode.valueOf(jsonValue.asString())
            }

            jsonValue.isTrue -> {
                TRUE
            }

            jsonValue.isFalse -> {
                BooleanNode.FALSE
            }

            jsonValue.isNull -> {
                NullNode.instance
            }

            jsonValue.isNumber -> {
                DoubleNode.valueOf(jsonValue.asDouble())
            }

            jsonValue.isArray -> {
                val arrayNode = om.createArrayNode()
                jsonValue.asArray().forEach {
                    arrayNode.add(rjsonToJackson(it))
                }
                arrayNode
            }

            jsonValue.isObject -> {
                val objectNode = om.createObjectNode()
                jsonValue.asObject().forEach {
                    objectNode.set<JsonNode>(it.name, rjsonToJackson(it.value))
                }
                objectNode
            }

            else -> error("can not handle")
        }

    }
}