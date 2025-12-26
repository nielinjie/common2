package xyz.nietongxue.common.scripts

import org.assertj.core.api.Assertions.assertThat
import xyz.nietongxue.common.base.stuff
import kotlin.test.Test

class TestGroovy {
    @Test
    fun testRun() {
        val script = """
            def a =1
            def b=2
            return a+b
        """.trimIndent()
        val result = groovyRun(script, stuff())
        assertThat(result.first).isEqualTo(3)
    }

    @Test
    fun testRunContext() {
        val script = """
            def b=2
            return a+b
        """.trimIndent()
        val result = groovyRun(script, mapOf("a" to 1))
        assertThat(result.first).isEqualTo(3)
    }

    @Test
    fun testRunComplex() {
        val script = """
           import xyz.nietongxue.common.query.Query 

            def filterString = "[{and:{name:{eq:alice}}}]"
            def filterJson = xyz.nietongxue.common.json.autoParse.ParseKt.autoParse(filterString)
            def filter = xyz.nietongxue.common.query.JsonKt.jsonToFilter(filterJson.first)
            def query = new Query(filter,null,null)
            query.toString()
        """.trimIndent()
        val result = groovyRun(script, mapOf("a" to 1))
        println(result.first)
    }
}