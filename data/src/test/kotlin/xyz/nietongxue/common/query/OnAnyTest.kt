package xyz.nietongxue.common.query

import com.fasterxml.jackson.databind.node.ArrayNode
import org.assertj.core.api.Assertions.assertThat
import xyz.nietongxue.common.json.autoParse.autoParse
import kotlin.test.Test

class OnAnyTest {
    @Test
    fun test() {
        val objects = listOf(
            mapOf("name" to "Alice", "age" to 18),
            mapOf("name" to "Bob", "age" to 34),
        )
        val filter = natureJsonToFilter(
            """
            [and: {name : {eq: "Alice"}},and: {age : {ne: 34}}]
        """.trimIndent().let { autoParse(it).first as ArrayNode })

        val filtering = Filtering(
            filter = filter,
            fields = object : Fields {
                override fun get(obj: Any, fieldName: String): Any {
                    require(obj is Map<*, *>)
                    return obj[fieldName]!!
                }
            }
        )
        val result = filtering.filter(objects)
        assertThat(result).isEqualTo(listOf(objects[0]))
    }
}