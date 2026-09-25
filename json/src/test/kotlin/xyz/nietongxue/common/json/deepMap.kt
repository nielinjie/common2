package xyz.nietongxue.common.json

import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.Test

class DeepMapTest {
    val string = """
        {
        "0":"kala",
        "1":"kala2",
            "a": {
                "b": "waht?","c":"keneng"
            }
        }
    """.trimIndent()


    val flatObject = """{\"b\": \"waht?\", \"c\": \"keneng\"}"""
    val stringFlatten = """
        {
        "0":"kala",
        "1":"kala2",
            "a": "$flatObject"
        }
    """.trimIndent()

    @Test
    fun test() {
        val deepMap = defaultOM.readValue<Map<String, Any>>(string)
        println(deepMap)
        deepMap.forEach { string, any ->
            println("$string: $any")
            println(any.javaClass)
        }
    }

    @Test
    fun testFlatten() {
        val deepMap = defaultOM.readValue<Map<String, Any>>(stringFlatten)
        println(deepMap)
        deepMap.forEach { (string, any) ->
            println("$string: $any")
            println(any.javaClass)
        }
        val unFlattenMap = deepMap.tryUnFlattenJson()
        println(unFlattenMap)
        unFlattenMap.forEach { (string, any) ->
            println("$string: $any")
            println(any.javaClass)
        }
    }


}