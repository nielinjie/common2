package xyz.nietongxue.common.schema.parse

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.BooleanNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode
import xyz.nietongxue.common.schema.ArraySchema
import xyz.nietongxue.common.schema.BooleanSchema
import xyz.nietongxue.common.schema.DataSchema
import xyz.nietongxue.common.schema.ObjectSchema
import xyz.nietongxue.common.schema.PrimitiveSchema
import xyz.nietongxue.common.schema.json.Format
import xyz.nietongxue.common.schema.json.autoParse

fun parseData(json: JsonNode, format: Format): DataSchema {
    return when (json) {
        is BooleanNode -> BooleanSchema(json.booleanValue())
        is TextNode -> json.textValue().let {
            if (it.lowercase() == "true" || it.lowercase() == "false")
                BooleanSchema(it.toBoolean())
            else
                PrimitiveSchema.Companion.fromString(it, format)
        }
            ?: error("primitiveSchema parse failed - ${json.toPrettyString()}")

        is ArrayNode -> ArraySchema(parseData(json.get(0)!!, format))
        is ObjectNode -> ObjectSchema(json.properties().associate { it.key to parseData(it.value, format) })
        else -> error("not recognized json node - ${json.toPrettyString()}")
    }
}

fun parseData(json: String, format: Format? = null): DataSchema {
    return if (format == null) autoParse(json).let {
        parseData(it.first, it.second)
    } else {
        parseData(format.json(json), format)
    }
}


