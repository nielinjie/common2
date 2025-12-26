package xyz.nietongxue.common.scripts

import kotlin.test.Test

class TestKotlin {
    @Test
    fun testRunComplex() {
        val script = """
           import xyz.nietongxue.common.query.Query 
           import com.fasterxml.jackson.databind.node.ArrayNode

            val filterString = "[{and:{name:{eq:alice}}}]"
            val filterJson = xyz.nietongxue.common.json.autoParse.autoParse(filterString)
            val filter = xyz.nietongxue.common.query.jsonToFilter(filterJson.first as ArrayNode)
            val query =  Query(filter,null,null)
             query.toString()
        """.trimIndent()
        val result = kotlinRun(script, mapOf("a" to 1))
        println(result.first)
    }
}