package xyz.nietongxue.common.schema.parse

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.*
import xyz.nietongxue.common.json.autoParse.Format
import xyz.nietongxue.common.json.autoParse.autoParse
import xyz.nietongxue.common.schema.*

@Deprecated("内部使用，外部请使用 parseDataSchema ")
fun parseData(json: JsonNode, format: Format): DataSchema {
    return when (json) {
        is BooleanNode -> Schemas.boolean()
        is IntNode -> Schemas.int()
        is LongNode -> Schemas.int()
        is DoubleNode -> Schemas.number()
        is FloatNode -> Schemas.number()
        is TextNode -> json.textValue().let {

            PrimitiveSchema.fromString(it, format)
        }
            ?: error("primitiveSchema parse failed - ${json.toPrettyString()}")

        is ArrayNode -> ArraySchema(parseData(json.get(0)!!, format))
        is ObjectNode -> ObjectSchema(json.properties().associate { it.key to parseData(it.value, format) })
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


