package xyz.nietongxue.common.softdata

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.jayway.jsonpath.JsonPath
import xyz.nietongxue.common.properties.transform
import xyz.nietongxue.common.softdata.path.Path
import xyz.nietongxue.common.softdata.path.PathNode
import xyz.nietongxue.common.string.wrapBy
import xyz.nietongxue.common.json.transform as ot

class Transforming(
    val path: Path,
    val propertyFn: (BuildingProperty) -> BuildingProperty,
    val valueFn: (Any?) -> Any?
)

fun Path.toJsonPath(): JsonPath {
    return (listOf("$") + this.path.map {
        when (it) {
            is PathNode.NameNode -> it.name.wrapBy("'").wrapBy("[")
            is PathNode.IndexNode -> it.index.toString().wrapBy("[")
            is PathNode.AllItemNode -> "*".wrapBy("[")
        }
    }).joinToString(".").let {
        JsonPath.compile(it)
    }
}


/*
TODO, 这里，都是用 json 实现的，肯定会比较慢。
 */
fun Any?.transform(transforming: Transforming): Any? {
    require(this != null)
    val om = jacksonObjectMapper()
    val o = om.valueToTree<ObjectNode>(this)
    return o.ot(
        listOf(transforming.path.toJsonPath()),
        fn = { json, path ->
            transforming.valueFn(om.treeToValue(json, Any::class.java))!!.let<Any, JsonNode> {
                om.valueToTree(it)
            } to path
        }
    ).let { om.treeToValue(it, Any::class.java) }
}

/**
 * TODO 似乎没有明确的意义。
 * value 的 transform 是明确的，但 type（property）的 transform作用是什么？用来验证 transform 以后的结果？
 *
 */
@Deprecated("意义不太明确")
fun DataStructure.transform(transforming: Transforming): DataStructure {

    return DataStructure(
        rootProperty = rootProperty.transform(transforming.path, transforming.propertyFn)
    ).also {
        it.value = this.value.transform(transforming)
    }

}

