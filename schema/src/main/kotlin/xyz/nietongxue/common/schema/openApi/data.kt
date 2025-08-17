package xyz.nietongxue.common.schema.openApi

import com.fasterxml.jackson.databind.node.BooleanNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.treeToValue
import io.swagger.v3.oas.models.media.Schema
import xyz.nietongxue.common.schema.DataSchema
import xyz.nietongxue.common.schema.jsonSchema.toJsonSchema

fun DataSchema.toOpenApi(): Schema<Any> {
    return this.toJsonSchema().toOpenApi()
}

fun ObjectNode.toOpenApi(): Schema<Any> {
    val om = jacksonObjectMapper()
    val additional = this.get("additionalProperties")
    val noNested = this.deepCopy()
        .without<ObjectNode>("additionalProperties") //TODO add other type of nested, + 不是 nest 的原因，可能是 null 的原因。additional properties 不能 set null
    return om.treeToValue<Schema<Any>>(noNested).apply {
        additional?.also {
            when (it) {
                is ObjectNode -> this.additionalProperties = it.toOpenApi()
                is BooleanNode -> this.additionalProperties = it.booleanValue()
            }
        }
    }
}
