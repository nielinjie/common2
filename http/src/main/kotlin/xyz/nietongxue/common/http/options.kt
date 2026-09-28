package xyz.nietongxue.common.http


data class CallOption(
    val inputMapping: InputMapping = InputMapping.allInParams,
    val method: RequestMethod = RequestMethod.POST,
)

/**
 * | 对比项 | @RequestParam | @RequestBody |
 * |---|---|---|
 * | 数据来源 | URL 查询参数 / 表单 application/x-www-form-urlencoded | HTTP 请求体 |
 * | 常见 Content-Type | application/x-www-form-urlencoded（默认表单） | application/json、application/xml |

 * 输入参数映射到请求的哪个位置,
 * Form和Body不能同时存在。
 * Header中携带Bearer, 特殊处理。所以来说并不局限于简单的存放位置，也包括一些常见的处理。
 * 后续改成一个更丰富的接口。
 */
enum class InputToPlace {
    Query, //url query param
    Form, // 表单数据， application/x-www-form-urlencoded，作为请求体，与 Body 互斥。
    Body, // 请求体，application/json，与 Form 互斥。
    Header, Path, Cookie,
    HeaderBear // Header中携带Bearer 认证, 特殊处理。所以来说并不局限于简单的存放位置，也包括一些常见的处理。
}


data class InputMapping(
    val specialFields: Map<InputToPlace, List<String>> = mapOf(),
    val default: InputToPlace = InputToPlace.Query
) {
    init {
        if (default == InputToPlace.Body && specialFields.containsKey(InputToPlace.Form))
            throw IllegalArgumentException("InputToPlace.Body and InputToPlace.Form cannot be used at the same time")
        if (default == InputToPlace.Form && specialFields.containsKey(InputToPlace.Body))
            throw IllegalArgumentException("InputToPlace.Body and InputToPlace.Form cannot be used at the same time")
        if (specialFields.containsKey(InputToPlace.Body) && specialFields.containsKey(InputToPlace.Form))
            throw IllegalArgumentException("InputToPlace.Body and InputToPlace.Form cannot be used at the same time")
    }

    fun setPlace(place: InputToPlace, vararg fields: String): InputMapping {
        if (place == this.default) return this
        return copy(
            specialFields = specialFields + (place to ((specialFields[place] ?: listOf()) + fields.toList()).distinct())
        )
    }

    fun getByField(field: String): InputToPlace {
        for ((place, fields) in specialFields) {
            if (fields.contains(field)) return place
        }
        return this.default
    }


    companion object {
        val allInParams = InputMapping()
        val inBody = InputMapping(default = InputToPlace.Body)
    }

}

enum class RequestMethod {
    GET, POST, PUT, DELETE, PATCH;

    companion object {
        @JvmStatic
        fun fromString(method: String): RequestMethod {
            return entries.find { it.name.equals(method, ignoreCase = true) }
                ?: throw IllegalArgumentException("未知的 RequestMethod：可选值为: ${entries.joinToString { it.name }}")
        }
    }
}