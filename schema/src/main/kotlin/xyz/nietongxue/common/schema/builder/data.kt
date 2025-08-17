package xyz.nietongxue.common.schema.builder

import xyz.nietongxue.common.schema.ArraySchema
import xyz.nietongxue.common.schema.Constraint
import xyz.nietongxue.common.schema.DataSchema
import xyz.nietongxue.common.schema.NamedType
import xyz.nietongxue.common.schema.ObjectSchema
import xyz.nietongxue.common.schema.PrimitiveSchema

fun String.nameType(): PrimitiveSchema {
    return PrimitiveSchema(listOf(NamedType("string")))
}

fun PrimitiveSchema.and(other: Constraint): PrimitiveSchema {
    return PrimitiveSchema(constraints + other)
}

fun DataSchema.array(): ArraySchema {
    return ArraySchema(this)
}
fun Map<String, DataSchema>.`object`(): ObjectSchema{
    return ObjectSchema(this)
}
fun Map<String, DataSchema>.obj(): ObjectSchema{
    return ObjectSchema(this)
}