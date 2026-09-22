package xyz.nietongxue.common.http


data class CallOption(
    val inputMapping: InputMapping = InputMapping.allParams,
    val method: RequestMethod = RequestMethod.POST,
)

/**
 * | 对比项 | @RequestParam | @RequestBody |
 * |---|---|---|
 * | 数据来源 | URL 查询参数 / 表单 application/x-www-form-urlencoded | HTTP 请求体 |
 * | 常见 Content-Type | application/x-www-form-urlencoded（默认表单） | application/json、application/xml |
 */
/**
 * 输入参数映射到请求的哪个位置,
 * Form和Body不能同时存在。
 * Header中携带Bearer, 特殊处理。所以来说并不局限于简单的存放位置，也包括一些常见的处理。
 * 后续改成一个更丰富的接口。
 */
enum class InputToPlace {
    Query, Form, Body, Header, Path, Cookie,
    HeaderBear // Header中携带Bearer, 特殊处理。所以来说并不局限于简单的存放位置，也包括一些常见的处理。
}


data class InputMapping(
    val otherFields: Map<InputToPlace, List<String>> = mapOf(),
    val default: InputToPlace = InputToPlace.Query
) {
    init {
        if (default == InputToPlace.Body && otherFields.containsKey(InputToPlace.Form))
            throw IllegalArgumentException("InputToPlace.Body and InputToPlace.Form cannot be used at the same time")
        if (default == InputToPlace.Form && otherFields.containsKey(InputToPlace.Body))
            throw IllegalArgumentException("InputToPlace.Body and InputToPlace.Form cannot be used at the same time")
        if(otherFields.containsKey(InputToPlace.Body) && otherFields.containsKey(InputToPlace.Form))
            throw IllegalArgumentException("InputToPlace.Body and InputToPlace.Form cannot be used at the same time")
    }

    fun others(place: InputToPlace, vararg fields: String): InputMapping {
        return copy(
            otherFields = otherFields + (place to ((otherFields[place] ?: listOf()) + fields.toList()).distinct())
        )
    }

    fun getByField(field: String): InputToPlace {
        for ((place, fields) in otherFields) {
            if (fields.contains(field)) return place
        }
        return this.default
    }

    companion object {
        val allParams = InputMapping()
    }

}

enum class RequestMethod {
    GET, POST;

    companion object {
        @JvmStatic
        fun fromString(method: String): RequestMethod {
            return entries.find { it.name.equals(method, ignoreCase = true) }
                ?: throw IllegalArgumentException("未知的 RequestMethod：可选值为: ${entries.joinToString { it.name }}")
        }
    }
}