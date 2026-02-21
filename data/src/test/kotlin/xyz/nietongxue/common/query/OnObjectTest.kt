package xyz.nietongxue.common.query

import com.fasterxml.jackson.databind.node.ArrayNode
import org.assertj.core.api.Assertions.assertThat
import xyz.nietongxue.common.json.autoParse.autoParse
import kotlin.test.Test

class OnObjectTest {
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

            fieldsType = object : Fields<Map<String, Any>> {
                override fun getFieldNames(): List<String> {
                    return listOf("name", "age")
                }

                override fun get(obj: Map<String, Any>, fieldName: String): Any {
                    return obj[fieldName]!!
                }

                override fun getFieldType(fieldName: String): String {
                    return when (fieldName) {
                        "name" -> "string"
                        "age" -> "number"
                        else -> throw IllegalArgumentException()
                    }
                }
            }
        )
        val result = filtering.filter(objects)
        assertThat(result).isEqualTo(listOf(objects[0]))
    }
}