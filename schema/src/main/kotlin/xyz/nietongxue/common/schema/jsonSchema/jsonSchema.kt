package xyz.nietongxue.common.schema.jsonSchema

import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import xyz.nietongxue.common.schema.ArraySchema
import xyz.nietongxue.common.schema.DataSchema
import xyz.nietongxue.common.schema.ObjectSchema
import xyz.nietongxue.common.schema.PrimitiveSchema
import kotlin.collections.iterator


val defaultAnnotation = $$"$schema" to "https://json-schema.org/draft/2020-12/schema"


fun DataSchema.toJsonSchema(annotation: Pair<String, String>? = defaultAnnotation): ObjectNode {
    val om = jacksonObjectMapper()
    return when (this) {
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