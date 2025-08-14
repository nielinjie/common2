package xyz.nietongxue.common.collections

import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import xyz.nietongxue.common.base.Attrs
import kotlin.collections.iterator

typealias PropertyList = Attrs<Any>
typealias Stuff = PropertyList

fun Map<String, Any>.toAllLevelMutable(): MutableMap<String, Any> {
    val result = mutableMapOf<String, Any>()
    for ((key, value) in this) {
        result[key] = value.let {
            if (it is Map<*, *>) {
                (it as Map<String, Any>).toAllLevelMutable()
            } else {
                it
            }
        }
    }
    return result
}

fun MutableMap<String, Any>.nestedPut(path: String, value: Any) {
    val keys = path.split(".")
    val lastKey = keys.last()
    val parent = keys.dropLast(1)
        .fold(this) { acc, key ->
            acc.computeIfAbsent(key) {
                mutableMapOf<String, MutableMap<String, Any>>()
            } as MutableMap<String, Any>
        }
    parent[lastKey] = value
}

fun Stuff.toJsonString(): String {
    val logger = org.slf4j.LoggerFactory.getLogger(Stuff::class.java)
    logger.info("Stuff toJsonString input: $this")
    return jacksonObjectMapper().valueToTree<ObjectNode>(this)!!.toString().also {
        logger.info("Stuff toJsonString: output $it")
    }
}

fun List<Stuff>.toJsonString(): String {
    val logger = org.slf4j.LoggerFactory.getLogger(Stuff::class.java)
    logger.info("List<Stuff> toJsonString input: $this")
    return jacksonObjectMapper().valueToTree<ArrayNode>(this)!!.toString().also {
        logger.info("List<Stuff> toJsonString: output $it")
    }
}

fun List<Any>.toJsonStringForAny(): String {
    val logger = org.slf4j.LoggerFactory.getLogger(Stuff::class.java)
    logger.info("List<Any> toJsonString input: $this")
    return jacksonObjectMapper().valueToTree<ArrayNode>(this)!!.toString().also {
        logger.info("List<Any> toJsonString: output $it")
    }
}


fun Any.toJsonString(pretty: Boolean = false): String {
    return jacksonObjectMapper().valueToTree<ObjectNode>(this).let {
        if (pretty) {
            it.toPrettyString()
        } else {
            it.toString()
        }
    }
}

fun List<Pair<String, Any?>>.toNotNullMap(): Map<String, Any> {
    return this.filter { it.second != null }.associate { it.first to it.second!! }
}