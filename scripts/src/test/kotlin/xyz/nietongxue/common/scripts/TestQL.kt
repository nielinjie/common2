package xyz.nietongxue.common.scripts

import org.assertj.core.api.Assertions.assertThat
import xyz.nietongxue.common.base.stuff
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
}