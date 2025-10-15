package xyz.nietongxue.common.schema.openApi

import xyz.nietongxue.common.schema.jsonSchema.toJsonSchema
import xyz.nietongxue.common.schema.parse.parseData
import xyz.nietongxue.common.testing.pretty
import kotlin.test.Test


class OpenApiTest {

    @Test
    fun testJsonSchema(){
        val string = "{type:string, data:{}}"
        val schema = parseData( string)
        schema.toJsonSchema().also {
            println(it.toPrettyString())
        }
    }
    @Test
    fun testJsonSchema2(){
        val string = "{type:string, data:{ _: false }}"
        val schema = parseData( string)
        schema.toJsonSchema().also {
            println(it.toPrettyString())
        }
    }
}