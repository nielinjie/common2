package xyz.nietongxue.common.schema.parse

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode
import xyz.nietongxue.common.json.autoParse.Format
import xyz.nietongxue.common.json.autoParse.autoParse
import xyz.nietongxue.common.schema.ArraySchema
import xyz.nietongxue.common.schema.DataSchema
import xyz.nietongxue.common.schema.ObjectSchema
import xyz.nietongxue.common.schema.PrimitiveSchema


fun DataSchema.toDeclareString(): String {
    return when (this) {
        is PrimitiveSchema -> this.toDeclareString()
        is ArraySchema -> "[${this.itemSchema.toDeclareString()}]"
        is ObjectSchema -> this.properties.map { "\"${it.key}\": \"${it.value.toDeclareString()}\"" }
            .joinToString(prefix = "{", postfix = "}", separator = ",") //TODO 处理 additionalProperties
        else -> error("not recognized data schema - $this")
    }
}

internal fun parseData(json: JsonNode, format: Format): DataSchema {
    return when (json) {
        is TextNode -> json.textValue().let {

            PrimitiveSchema.fromDeclareString(it, format)
        }
            ?: error("primitiveSchema parse failed - ${json.toPrettyString()}")

        is ArrayNode -> ArraySchema(parseData(json.get(0)!!, format))
        is ObjectNode -> ObjectSchema(
            json.properties().associate {
                it.key to parseData(
                    it.value,
                    format
                )
            }) //TODO if it.key="_", then it's additionalProperties 按照 it.value 处理。
        else -> error("not recognized json node - ${json.toPrettyString()}")
    }
}


internal fun parseData(json: String, format: Format? = null): DataSchema {
    return if (format == null) autoParse(json).let {
        parseData(it.first, it.second)
    } else {
        parseData(format.json(json), format)
    }
}

/**
 * 主要对外方法
 * jsonString 可以是 json、json5、rjson、yaml等。
 */
fun parseDataSchema(jsonString: String): DataSchema {
    return parseData(jsonString)
}


