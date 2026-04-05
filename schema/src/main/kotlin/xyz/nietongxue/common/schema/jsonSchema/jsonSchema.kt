package xyz.nietongxue.common.schema.jsonSchema

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.BooleanNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.swagger.v3.oas.models.media.Schema
import xyz.nietongxue.common.schema.ArraySchema
import xyz.nietongxue.common.schema.BooleanSchema
import xyz.nietongxue.common.schema.DataSchema
import xyz.nietongxue.common.schema.ObjectSchema
import xyz.nietongxue.common.schema.PrimitiveSchema
import xyz.nietongxue.common.schema.Required
import xyz.nietongxue.common.schema.isRequired
import xyz.nietongxue.common.schema.required
import xyz.nietongxue.common.schema.thisConstraints
import kotlin.collections.iterator


val defaultAnnotation = $$"$schema" to "https://json-schema.org/draft/2020-12/schema"
/*
TODO
如何表示：
object，但没有其他限制。
additionalProperties :true
不是 false，也没有 schema。
 */


fun DataSchema.toSwagger(): Schema<out Any> {
    return when (this) {
        is PrimitiveSchema -> when (this.typeName()) {
            "string" -> Schema<String>().apply {
                this.addType("string")
            }

            "number" -> Schema<Double>().apply {
                this.addType("number")
            }

            "boolean" -> Schema<Boolean>().apply {
                this.addType("boolean")
            }

            else -> error("not support")
        }.let {
            //TODO primitive constraints
            it.also {
                if (!this@toSwagger.thisConstraints().isRequired()) {
                    it.addType("null")
                }
            }
        }

        is ObjectSchema -> Schema<Any>().apply {
            this.addType("object")
            if (!this@toSwagger.thisConstraints().isRequired()) {
                this.addType("null")
            }
            //TODO object constraints
            this.properties = this@toSwagger.properties.mapValues { it.value.toSwagger() }.toMutableMap()
            this.required(this@toSwagger.properties.mapNotNull {
                if (it.value.thisConstraints().isRequired()) it.key else null
            })
            this.additionalProperties = this@toSwagger.additionalProperties?.schema?.toSwagger() ?: false
        }

        is ArraySchema -> Schema<Any>().apply {
            this.addType("array")
            if (!this@toSwagger.thisConstraints().isRequired()) {
                this.addType("null")
            }
            //TODO array constraints
            this.items = this@toSwagger.itemSchema.toSwagger()
        }

        else -> error("not support")
    }
}

fun DataSchema.toJsonSchema(): ObjectNode {
    return this.toSwagger().toJsonSchema()
}