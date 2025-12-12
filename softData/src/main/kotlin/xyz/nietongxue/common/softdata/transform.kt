package xyz.nietongxue.common.softdata

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.jayway.jsonpath.JsonPath
import xyz.nietongxue.common.json.JPath
import xyz.nietongxue.common.json.JPathNode
import xyz.nietongxue.common.properties.Property
import xyz.nietongxue.common.properties.transform
import xyz.nietongxue.common.string.wrapBy
import kotlin.plus
import xyz.nietongxue.common.json.transform as ot

class Transforming(
    val path: TransformPath,
    val propertyFn: (BuildingProperty) -> BuildingProperty,
    val valueFn: (Any?) -> Any?
)

fun TransformPath.toJsonPath(): JsonPath {
    (listOf("$") + this.path.map {
        when (it) {
            is TransformPathNode.NamePathNode -> it.name.wrapBy("'").wrapBy("[")
            is TransformPathNode.IndexPathNode -> it.index.toString().wrapBy("[")
            is TransformPathNode.ItemPathNode -> "*".wrapBy("[")
        }
    }).joinToString(".").let {
        return JsonPath.compile(it)
    }
}


fun DataStructure.transform(transforming: Transforming): DataStructure {
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
    return DataStructure(
        rootProperty = rootProperty.transform(transforming.path, transforming.propertyFn)
    ).also {
        it.value = this.value.transform(transforming)
    }

}

