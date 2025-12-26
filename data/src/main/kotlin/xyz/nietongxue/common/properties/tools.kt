package xyz.nietongxue.common.properties

import xyz.nietongxue.common.schema.ArraySchema
import xyz.nietongxue.common.schema.DataSchema
import xyz.nietongxue.common.schema.ObjectSchema
import xyz.nietongxue.common.schema.PrimitiveSchema

// 分解，有时候需要根据 schema 进行分解，使每一级都是 primarySchema 的属性。
fun Property.decompose(): Property {
    return when (this) {
        is ObjectProperty -> {
            val newProperties = this.properties.mapValues {
                it.value.decompose()
            }
            ObjectProperty(newProperties)
        }

        is ArrayProperty -> {
            val newItem = this.item.decompose()
            ArrayProperty(newItem)
        }

        is SimpleProperty -> this.schema.buildProperty()

        else -> error("not supported property")
    }
}

fun DataSchema.buildProperty(): Property {
    return when (this) {
        is PrimitiveSchema -> SimpleProperty(this)
        is ObjectSchema -> ObjectProperty(this.properties.mapValues { it.value.buildProperty() })
        is ArraySchema -> ArrayProperty(this.itemSchema.buildProperty())
        else -> error("not supported schema type")
    }
}