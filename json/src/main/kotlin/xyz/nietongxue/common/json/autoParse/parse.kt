package xyz.nietongxue.common.json.autoParse

import com.fasterxml.jackson.databind.JsonNode

fun autoParse(json: String): Pair<JsonNode, Format> {
    return runCatching {
        RJsonFormat.let {
            it.json(json) to it
        }
    }.getOrElse {
        Yaml.let {
            it.json(json) to it
        }
    }
}