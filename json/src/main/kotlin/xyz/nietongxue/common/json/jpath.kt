package xyz.nietongxue.common.json

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.NullNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.jayway.jsonpath.*
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider
import com.jayway.jsonpath.spi.json.JsonProvider
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider
import com.jayway.jsonpath.spi.mapper.MappingProvider
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*


class JPathConfig {
    init {
        Configuration.setDefaults(object : Configuration.Defaults {
            private val jsonProvider: JsonProvider = JacksonJsonNodeJsonProvider()
            private val mappingProvider: MappingProvider = JacksonMappingProvider()

            override fun jsonProvider(): JsonProvider {
                return jsonProvider
            }

            override fun mappingProvider(): MappingProvider {
                return mappingProvider
            }

            override fun options(): Set<Option> {
                return EnumSet.noneOf(Option::class.java)
            }
        })
    }
}

sealed interface JPathNode {
    data class NameNode(val name: String) : JPathNode
    data class IndexNode(val index: Int) : JPathNode
}


fun ObjectNode.read(jpath: JPath): JsonNode {
    return jpath.read(JsonPath.parse(this))
}

fun <T> ObjectNode.read(jpath: JPath, clazz: Class<T>): T {
    return jpath.read(JsonPath.parse(this), clazz)
}


/*
JPath 主要用于 setting。不支持通配符、查询等。
读取的话，用 JsonPath 。
*/
data class JPath(val parts: List<JPathNode>) {
    val logger = LoggerFactory.getLogger(JPath::class.java)
    fun append(node: JPathNode): JPath {
        return JPath(parts + node)
    }

    fun read(json: JsonNode): JsonNode {
        return read(JsonPath.parse(json))
    }

    fun readSilently(json: JsonNode, default: JsonNode? = null): JsonNode? {
        return try {
            read(json)
        } catch (e: PathNotFoundException) {
            logger.trace("Path not found: {}", this)
            default
        }
    }

    fun read(json: DocumentContext): JsonNode {
        return json.read(jsonPath)
    }

    fun <T> read(json: DocumentContext, clazz: Class<T>): T {
        return json.read(jsonPath, clazz)
    }

    fun pathExists(json: DocumentContext): Boolean {
        try {
            val result = json.read<Any>(jsonPath)
            return !(result == null || result is NullNode)
        } catch (e: PathNotFoundException) {
            return false
        }
    }

    fun set(json: ObjectNode, value: Any): ObjectNode {
        if (isRoot()) {
            return (value as ObjectNode).deepCopy()
        }
        val doc = JsonPath.parse(json)
        set(doc, value)
        return (doc.json() as ObjectNode).deepCopy()
    }

    fun set(json: DocumentContext, value: Any) {
        if (isRoot()) {
            return
        }
        if (pathExists(json)) {
            json.set(jsonPath, value)
        } else {
            val (parentPath, node) = moveUp()
            if (!parentPath.pathExists(json)) {
                parentPath.set(json, defaultParentValue(node))
            }
            when (node) {
                is JPathNode.NameNode -> json.put(parentPath.jsonPath, node.name, value)
                is JPathNode.IndexNode -> {

                    if (node.index == 0) {
                        json.add(parentPath.jsonPath, value)
                    } else {
                        for (i in 0 until node.index) {
                            if (parentPath.append(JPathNode.IndexNode(i)).pathExists(json)) {
                                continue
                            }
                            json.add(parentPath.jsonPath, defaultSiblingValue(value))
                        }
                        json.add(parentPath.jsonPath, value)
                    }

                }
            }
        }
    }

    private fun defaultParentValue(node: JPathNode): Any {
        return when (node) {
            is JPathNode.NameNode -> ObjectNode(JsonNodeFactory.instance)
            is JPathNode.IndexNode -> ArrayNode(JsonNodeFactory.instance)
        }
    }

    private fun defaultSiblingValue(value: Any): Any {
        return when (value) {
            is Map<*, *> -> ObjectNode(JsonNodeFactory.instance)
            is List<*> -> ArrayNode(JsonNodeFactory.instance)
            else -> ObjectNode(JsonNodeFactory.instance)
        }
    }

    fun parentOrNull(): JPath? {
        return if (parts.size > 1) {
            JPath(parts.dropLast(1))
        } else {
            null
        }
    }

    private fun moveUp(): Pair<JPath, JPathNode> {
        return Pair(JPath(parts.dropLast(1)), parts.last())
    }

    fun isRoot(): Boolean {
        return parts.isEmpty()
    }


    private val jsonPath: String by lazy {
        toJsonPath()
    }

    fun toJsonPath(): String {
        return parts.joinToString("") {
            when (it) {
                is JPathNode.NameNode -> "['${it.name}']"
                is JPathNode.IndexNode -> "[${it.index}]"
                else -> error("Invalid path node: $it")
            }
        }.let {
            "\$$it"
        }

    }

    fun concrete(): Boolean {
        return parts.all {
            when (it) {
                is JPathNode.NameNode -> it.name.isNotEmpty()
                        && it.name.isNotBlank()
                        && (listOf("[", "]", ".", "'", "@", "(", ")", "*").none { s -> s in it.name })

                is JPathNode.IndexNode -> it.index >= 0
            }
        }
    }

    companion object {

        private var useDefaultConfig: JPathConfig? = null
        private fun String.toJPathNode(): JPathNode {
            return this.toIntOrNull()?.let {
                JPathNode.IndexNode(it)
            } ?: if (this.startsWith("'") && this.endsWith("'")) {
                JPathNode.NameNode(this.substring(1, this.length - 1))
            } else {
                error("Invalid path node: $this")
            }
        }

        private fun parseInternal(path: String): JPath {
            val regexNode = """\[(.+?)]""".toRegex()
            val pathParts = regexNode.findAll(path)
                .map { it.groupValues[1] }.toList()
            return JPath(pathParts.map { it.toJPathNode() })
        }

        fun parse(jsonPath: JsonPath): JPath {
            return parseInternal(jsonPath.path).let {
                if (it.concrete()) {
                    it
                } else {
                    error("Invalid path: ${jsonPath.path}")
                }
            }
        }

        fun parse(whole: String): JPath {
            return parse(JsonPath.compile(whole))
        }

        fun parse(whole: List<String>): JPath {
            return parse(
                JsonPath.compile(
                    if (whole.firstOrNull() != "$") {
                        "$."
                    } else {
                        ""
                    } + whole.joinToString(".")
                )
            )
        }

        fun parse(whole: Array<String>): JPath {
            return parse(whole.toList())
        }


        fun useDefaultConfig() {
            if (useDefaultConfig == null) {
                useDefaultConfig = JPathConfig()
            }
        }

    }

}

fun ObjectNode.concretePaths(paths: List<JsonPath>, logger: Logger? = null): List<JPath> {
    val config = Configuration.defaultConfiguration().addOptions(Option.AS_PATH_LIST)
    return paths.map {
        runCatching { (JsonPath.using(config).parse(this).read(it) as ArrayNode) }.onFailure {
            logger?.error("Error when concretePaths:  $it")
        }.getOrNull()?.toList() ?: emptyList()
    }.flatten().map { JPath.parse(it.textValue()) }
}

//fun ObjectNode.concretePaths(paths: List<String>): List<JPath> {
//    return this.concretePaths(paths.map { JsonPath.compile(it) })
//}
