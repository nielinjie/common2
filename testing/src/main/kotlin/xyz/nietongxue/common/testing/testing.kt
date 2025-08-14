package xyz.nietongxue.common.testing

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.event.Level

fun pretty(result: Any) {
    val logger = LoggerFactory.getLogger("prettyPrinter")
    logger.pretty(result)
}

fun Logger.pretty(result: Any) {
    this.pretty(null, result)
}

fun Logger.pretty(templateString: String? = null, result: Any, level: Level = Level.INFO) {
    (if (result is Collection<*>) {
        jacksonObjectMapper().valueToTree<ArrayNode>(result)
    } else {
        jacksonObjectMapper().valueToTree<JsonNode>(result)
    }).toPrettyString().also {
        val string = result.javaClass.name + ":\n" + it
        val event = this.atLevel(level)
        if (templateString == null)
            event.log(string)
        else
            event.log(templateString, string)
    }
}