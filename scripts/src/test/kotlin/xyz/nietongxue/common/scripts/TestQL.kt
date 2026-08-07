package xyz.nietongxue.common.scripts

import com.fasterxml.jackson.module.kotlin.readValue
import org.assertj.core.api.Assertions.assertThat
import xyz.nietongxue.common.base.stuff
import xyz.nietongxue.common.json.defaultOM
import kotlin.test.Test

class TestQL {
    @Test
    fun testRun() {
        val result = ql4Run(
            """a=1
                b=2
                c=a+b
            """.trimMargin(), stuff()
        )
        assertThat(result.first).isEqualTo(3)
    }

    @Test
    fun testRunContext() {
        val result = ql4Run(
            """
                b=2
                c=a+b
            """.trimMargin(), stuff("a" to 1)
        )
        assertThat(result.first).isEqualTo(3)
    }

    @Test
    fun testAutoJson() {
        val json = """
            {"hello": "world"}
        """.trimIndent()
        val result = ql4Run(
            """
                json['hello']
            """.trimIndent(), stuff("json" to json.let {
                defaultOM.readValue<Map<String, Any>>(it)
            })
        )
        assertThat(result.first).isEqualTo("world")
    }
}