package xyz.nietongxue.common.schema.jsonSchema

import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode
import io.swagger.v3.core.converter.ModelConverters
import io.swagger.v3.core.converter.ResolvedSchema



fun jsonSchemaFromClass(type: Class<*>): ObjectNode {
    return toFullJsonSchema(schemaFromClass(type))
}

fun schemaFromClass(type: Class<*>): ResolvedSchema {
    return ModelConverters.getInstance().readAllAsResolvedSchema(type)
}

fun toFullJsonSchema(resolvedSchema: ResolvedSchema): ObjectNode {
    val root = resolvedSchema.schema.toJsonSchema()
    val defs = ObjectNode(JsonNodeFactory.instance)
    resolvedSchema.referencedSchemas.forEach { (name, schema) ->
        defs.set<ObjectNode>(name, schema.toJsonSchema())
    }
    if (defs.size() > 0) {
        root.set<ObjectNode>($$"$defs", defs)
    }
    // 将 Swagger/OpenAPI 风格的 $ref 路径转换为 JSON Schema 风格的 $defs 路径
    rewriteRefs(root)
    return root
}

private fun rewriteRefs(node: ObjectNode) {
    val ref = node.get($$"$ref")
    if (ref is TextNode) {
        val value = ref.textValue()
        if (value.startsWith("#/components/schemas/")) {
            val newRef = value.replace("#/components/schemas/", "#/\$defs/")
            node.put($$"$ref", newRef)
        }
    }
    node.properties().forEach { (_, value) ->
        when (value) {
            is ObjectNode -> rewriteRefs(value)
            is ArrayNode -> value.elements().forEach {
                if (it is ObjectNode) rewriteRefs(it)
            }
        }
    }
}