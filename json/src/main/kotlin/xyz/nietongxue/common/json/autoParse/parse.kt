package xyz.nietongxue.common.json.autoParse

import com.fasterxml.jackson.databind.JsonNode

fun autoParse(json: String): Pair<JsonNode, Format> {
    val formats = listOf(
        RJsonFormat ,
        Json5,NormalJson,Yaml,
    )

    formats.forEach { format ->
        runCatching {
            format.json(json)
        }.getOrNull()?.also { return Pair(it, format) }
    }
    error("Failed to parse json")
}