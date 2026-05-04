package xyz.nietongxue.common.base

import java.net.URI


private fun parseQueryParams(query: String?): Map<String, String> {
    if (query.isNullOrBlank()) return emptyMap()

    return query.split("&")
        .mapNotNull { param ->
            val parts = param.split("=", limit = 2)
            if (parts.size == 2) {
                parts[0] to parts[1]
            } else null
        }
        .toMap()
}

fun URI.queryAsParams(): Map<String, String> {
    return parseQueryParams(query)
}

private fun parseQueryParamsAdvanced(query: String?): Map<String, List<String>> {
    if (query.isNullOrBlank()) return emptyMap()

    return query.split("&")
        .mapNotNull { param ->
            val parts = param.split("=", limit = 2)
            if (parts.isNotEmpty()) {
                val key = java.net.URLDecoder.decode(parts[0], "UTF-8")
                val value = if (parts.size > 1)
                    java.net.URLDecoder.decode(parts[1], "UTF-8")
                else ""
                key to value
            } else null
        }
        .groupBy({ it.first }, { it.second })
}

fun URI.queryAsParamsMultiValue(): Map<String, List<String>> {
    return parseQueryParamsAdvanced(query)
}