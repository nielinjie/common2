package xyz.nietongxue.common.schema.parse

import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import xyz.nietongxue.common.schema.Operation
import xyz.nietongxue.common.schema.Parameter
import xyz.nietongxue.common.json.autoParse.Format
import xyz.nietongxue.common.json.autoParse.autoParse

fun parseOperation(json: String, format: Format? = null): Operation {
    return if (format == null) { //TODO 这里 parse 了两遍。
        parseOperation(json, autoParse(json).second)
    } else {
        val json = format.json(json) as ObjectNode
        var op = Operation()
        json.properties().forEach { (key, value) ->
            when (key) {
                "request" -> {
                    op = op.copy(request = parseData(value, format))
                }

                "response" -> {
                    op = op.copy(response = parseData(value, format))
                }

                "parameters" -> {
                    val parameters: List<Parameter> = (value as ArrayNode).map {
                        jsonToParameter(it as ObjectNode, format)
                    }
                    op = op.copy(parameters = parameters)
                }
            }
        }
        op
    }
}

fun jsonToParameter(json: ObjectNode, format: Format): Parameter {
    val om = jacksonObjectMapper()

    val schema = json.get("schema")?.let {
        parseData(it, format)
    } ?: error("no schema find")
    val name = json.get("name")?.textValue() ?: error("no name find")
    val required = json.get("required")?.booleanValue() ?: false
    val style = json.get("style")?.textValue() ?: "form"
    val `in` = json.get("in")?.textValue() ?: "query"
    return Parameter(name, schema, `in`, required, style)
}
