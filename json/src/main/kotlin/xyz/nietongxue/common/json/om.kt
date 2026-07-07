package xyz.nietongxue.common.json

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

val defaultOM = jacksonObjectMapper().also {
    it.registerModule(JavaTimeModule())
}