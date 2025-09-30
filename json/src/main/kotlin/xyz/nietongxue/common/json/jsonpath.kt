package xyz.nietongxue.common.json

import com.jayway.jsonpath.JsonPath


/**
 * 暂无更多的加强，主要是容易引入。
 */

fun String.jsonPath(): JsonPath {
    return JsonPath.compile(this)
}
fun JsonPath.read(json: String): Any? {
    return this.read(json)
}