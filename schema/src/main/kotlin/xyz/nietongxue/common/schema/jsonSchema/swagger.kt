package xyz.nietongxue.common.schema.jsonSchema

import com.fasterxml.jackson.databind.node.ObjectNode
import io.swagger.v3.core.util.AnnotationsUtils
import io.swagger.v3.core.util.Json31
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.oas.annotations.media.Schema as AS

fun <T : Any> Schema<T>.toJsonSchema(): ObjectNode {
    return Json31.mapper().valueToTree(this)
}

fun AS.toSchemaO(): Schema<Any> {
    return AnnotationsUtils.getSchemaFromAnnotation(this, null).get()
}