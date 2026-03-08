package xyz.nietongxue.common.schema.openApi

import io.swagger.v3.oas.models.media.ArraySchema
import io.swagger.v3.oas.models.media.Schema
import xyz.nietongxue.common.schema.jsonSchema.toJsonSchema
import kotlin.test.Test

class JsonSchemaTest {
    @Test
    fun testJsonSchema() {
        val schema = Schema<Any>().also {
            it.addType("string")
        }
        val json = schema.toJsonSchema()
        println(json)
    }
    @Test
    fun testJsonSchema2() {
        val schema = ArraySchema().also {
            it.items = Schema<Any>().also {
                it.addType("string")
            }
        }
        val json = schema.toJsonSchema()
        println(json)
    }
}