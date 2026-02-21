package xyz.nietongxue.common.schema.jsonSchema

import com.fasterxml.jackson.databind.jsonschema.SchemaAware
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.oas.annotations.media.Schema as AS

@Deprecated("对 swagger schema 不继续支持")
fun <T : Any> Schema<T>.toJsonSchema(): ObjectNode {
    val om = jacksonObjectMapper()
    return this.jsonSchema.let {
        om.valueToTree<ObjectNode>(it)
    }
}

@Deprecated("对 swagger schema 不继续支持")
fun AS.toSchema(): Schema<Any> {
    return Schema<Any>().also {
        it.name = name
        it.description = description
        it.type = type
    }
}