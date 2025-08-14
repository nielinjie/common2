package xyz.nietongxue.common.objects

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper


fun jsonObjectRewrite(
    origin: ObjectNode,
    rewrite: ObjectNode
): ObjectNode {
    return origin.deepCopy().also {no ->
        rewrite.properties().forEach {
            no.set<ObjectNode>(
                it.key, it.value
            )
        }
    }
}


/*
返回一个新对象，具有 origin 的属性并覆盖为 rewrite 的属性，origin 本身不会修改。
 */


fun <T : Any> objectRewriteByJson(
    origin: T,
    rewrite: Map<String, JsonNode>,
    om: ObjectMapper = jacksonObjectMapper()
): T {
    val tree = om.valueToTree<ObjectNode>(origin)
    rewrite.forEach { (k, v) ->
        tree.set<ObjectNode>(k, v)
    }
    return om.treeToValue(tree, origin::class.java)
}

fun <T : Any> objectRewrite(
    origin: T,
    rewrite: Map<String, Any>,
    om: ObjectMapper = jacksonObjectMapper()
): T {
    return objectRewriteByJson(origin, rewrite.mapValues { (_, v) -> om.valueToTree(v) }, om)
}

fun objectGet(
    origin: Any, propertyName: String, om: ObjectMapper = jacksonObjectMapper()
): JsonNode? {
    return om.valueToTree<ObjectNode>(origin).get(propertyName)
}