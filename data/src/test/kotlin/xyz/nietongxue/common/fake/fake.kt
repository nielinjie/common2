package xyz.nietongxue.common.fake

import xyz.nietongxue.common.schema.ObjectSchema
import xyz.nietongxue.common.schema.Schemas
import xyz.nietongxue.common.schema.withLengthRange
import kotlin.test.Test

class FakeTest {
    @Test
    fun test() {
        ObjectSchema(
            properties = mapOf(
                "name" to Schemas.string(),
                "age" to Schemas.int(),
                "birthday" to Schemas.date(),
                "address" to Schemas.objectSchema(
                    properties = mapOf(
                        "city" to Schemas.string().withLengthRange(3,5),
                        "street" to Schemas.string()
                    )
                )
            )
        ).also {
            val fake = it.fake()
            println(fake)
        }

    }
}