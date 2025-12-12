package xyz.nietongxue.common.json

import com.fasterxml.jackson.databind.node.TextNode
import com.jayway.jsonpath.JsonPath
import xyz.nietongxue.common.json.transform
import kotlin.test.Test

class TransformTest {

    @Test
    fun test() {
        JPathConfig()
        val json = jo {
            put("name", "Tom")
        }
        val new = json.transform(listOf("$.name").map { JsonPath.compile(it) }, { value, path ->
            TextNode("hello ${value!!.textValue()}") to path
        })
        println(new)
    }

    @Test
    fun test2() {
        JPathConfig()
        val json = jo {
            put("name", "Tom")
            put("address", "shanghai")
        }
        val new = json.transform(listOf("$.address").map { JsonPath.compile(it) }, { value, path ->
            ja(TextNode("hello ${value!!.textValue()}")) to path
        })
        println(new)
    }

    @Test
    fun test2Null() {
        JPathConfig()
        val json = jo {
            put("name", "Tom")
        }
        val new1 = JPath.parse("$.address").set(json,ja())
        println(new1)
        val new = new1.transform(listOf("$.address").map { JsonPath.compile(it) }, { value, path ->
            (value?.let { TextNode("hello ${it.textValue()}") }?.let { ja(it) } ?: ja()) to path
        })
        println(new)
    }


}
