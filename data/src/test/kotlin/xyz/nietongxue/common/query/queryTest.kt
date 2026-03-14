package xyz.nietongxue.common.query

import com.fasterxml.jackson.databind.node.ObjectNode
import xyz.nietongxue.common.json.autoParse.autoParse
import kotlin.test.Test

class QueryTest {
    @Test
    fun testSort() {
        val sort = """
            {sort:{name: asc}}
        """.trimIndent()
        val result = autoParse(sort).let {
            natureJsonToQuery(it.first as ObjectNode)
        }
        println(result)
    }
    @Test
    fun testSort2() {
        val sort = """
            {order:{name: asc,age: desc}}
        """.trimIndent()
        val result = autoParse(sort).let {
            natureJsonToQuery(it.first as ObjectNode)
        }
        println(result)
    }
    @Test
    fun testSort3() {
        val sort = """
            {order:[{name: asc},{age: desc}]}
        """.trimIndent()
        val result = autoParse(sort).let {
            natureJsonToQuery(it.first as ObjectNode)
        }
        println(result)
    }
}