package xyz.nietongxue.common.json

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.jayway.jsonpath.JsonPath

/*
使用 jsonpath 和 jpath 进行json的转换
 */

fun ObjectNode.transform(
    readPaths: List<JsonPath>,
    fn: (value: JsonNode?, path: JPath) -> Pair<JsonNode, JPath>
): ObjectNode {
    val concretePaths = this.concretePaths(readPaths)
    val transformedSetting = concretePaths.map {
        val value = it.readSilently(this)
        fn(value, it)
    }
    return transformedSetting.fold(this) { acc, pair ->
        val (value, path) = pair
        acc.deepCopy().let {
            path.set(it, value)
        }
    }
}



