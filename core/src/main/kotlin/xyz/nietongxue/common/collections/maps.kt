package xyz.nietongxue.common.collections

import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import xyz.nietongxue.common.base.Stuff
import kotlin.collections.iterator


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


fun MutableMap<String, Any>.nestedPut(keys: List<String>, value: Any) {
    val lastKey = keys.last()
    val parent = keys.dropLast(1)
        .fold(this) { acc, key ->
            acc.computeIfAbsent(key) {
                mutableMapOf<String, MutableMap<String, Any>>()
            } as MutableMap<String, Any>
        }
    parent[lastKey] = value
}

fun MutableMap<String, Any>.nestedPut(path: String, value: Any) {
    val keys = path.split(".")
    this.nestedPut(keys, value)
}

fun Map<String, Any?>.nestedGet(keys: List<String>): Any? {
    //AI生成
    var current: Any? = this
    for (key in keys) {
        current = (current as? Map<String, Any?>)?.get(key) ?: error("not a map")
    }
    return current
}

fun Map<String, Any?>.nestedGet(path: String): Any? {
    val keys = path.split(".")
    return this.nestedGet(keys)
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

fun <T : Any> List<Pair<T, Any?>>.toNotNullMap(): Map<T, Any> {
    return this.filter { it.second != null }.associate { it.first to it.second!! }
}

fun <T : Any> Map<T, Any>.singleOrNull(): Pair<T, Any>? {
    return this.entries.singleOrNull()?.toPair()
}


fun <T : Any> Map<T, Any?>.toNotNullMap(): Map<T, Any> {
    return this.toList().toNotNullMap()
}

fun <T : Any> Map<T, Any?>.toNoNullValueMap(): Map<T, Any> { //这个名字更准确些。
    return this.toList().toNotNullMap()
}

fun Map<String, String>.toMultiValueMap(): MultiValueMap<String, String> {
    val result = LinkedMultiValueMap<String, String>()
    this.forEach { (key, value) -> result.add(key, value) }
    return result
}
