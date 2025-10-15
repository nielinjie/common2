package xyz.nietongxue.common.schema.jsonSchema

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.BooleanNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import xyz.nietongxue.common.schema.ArraySchema
import xyz.nietongxue.common.schema.BooleanSchema
import xyz.nietongxue.common.schema.DataSchema
import xyz.nietongxue.common.schema.ObjectSchema
import xyz.nietongxue.common.schema.PrimitiveSchema
import kotlin.collections.iterator


val defaultAnnotation = $$"$schema" to "https://json-schema.org/draft/2020-12/schema"
/*
TODO
如何表示：
object，但没有其他限制。
additionalProperties :true
不是 false，也没有 schema。
 */

fun DataSchema.toJsonSchema(annotation: Pair<String, String>? = defaultAnnotation): JsonNode {
    val om = jacksonObjectMapper()
    return when (this) {
        is BooleanSchema -> {
            BooleanNode.valueOf(this.value)
        }

        is ObjectSchema -> {
            om.createObjectNode().also {
                annotation?.also { a ->
                    it.put(a.first, a.second)
                }
                it.put("type", "object")
                it.putObject("properties").also { properties ->
                    for ((key, value) in this.properties) {
                        if (key != "_")
                            properties.put(key, value.toJsonSchema(null))
                    }
                }
                this.additional()?.also { ap ->
                    it.put("additionalProperties", ap.schema.toJsonSchema(null))
                } ?: it.put("additionalProperties", false)
            }

        }

        is PrimitiveSchema -> {
            om.createObjectNode().also {
                it.put("type", this.typeName())
                //TODO add other constraints
            }
        }

        is ArraySchema -> {
            om.createObjectNode().also {
                it.put("type", "array")
                it.put("items", this.itemSchema.toJsonSchema(null))
            }
        }

        else -> error("not support")
    }
}