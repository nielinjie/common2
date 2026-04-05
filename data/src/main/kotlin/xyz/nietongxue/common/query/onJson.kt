package xyz.nietongxue.common.query

import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import xyz.nietongxue.common.collections.nestedGet

class NestableFields() : Fields {
    override fun get(obj: Any, fieldName: String): Any? {
        require(obj is Map<*, *>)
        return (obj as Map<String, Any>).nestedGet(fieldName)
    }
}

class JsonFiltering(filter: Filter) : Filtering(filter, NestableFields()) {
    fun filterJson(json: ArrayNode): List<Any> {
        val om = jacksonObjectMapper()
        return filter(json.toList().map {
            om.treeToValue(it, Map::class.java)
        })
    }
}
fun filterJson(json: ArrayNode, filter: Filter):List<Any>{
    return JsonFiltering(filter).filterJson(json)
}
