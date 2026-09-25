package xyz.nietongxue.common.json

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

val defaultOM = jacksonObjectMapper().also {
    it.registerModule(JavaTimeModule())
}

/**
 * 有时候，json字符串里面会嵌套json字符串，但本来应该是 object 套object
 */
fun Map<String, Any>.tryUnFlattenJson(): Map<String, Any> {
    return this.mapValues { (k, v) ->
        if (v is String)
            runCatching {
                defaultOM.readValue<Map<String, Any>>(v) //TODO 目前只有一层？需要处理嵌套情况？
            }.getOrElse { runCatching { defaultOM.readValue<List<Any>>(v) }.getOrElse { v } }
        else v
    }
}