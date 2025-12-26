package xyz.nietongxue.common.properties

import com.fasterxml.jackson.annotation.JsonIgnore
import xyz.nietongxue.common.base.Name
import xyz.nietongxue.common.json.JsonWithType
import xyz.nietongxue.common.schema.ArraySchema
import xyz.nietongxue.common.schema.DataSchema
import xyz.nietongxue.common.schema.NamedType
import xyz.nietongxue.common.schema.ObjectSchema
import xyz.nietongxue.common.schema.PrimitiveSchema

@JsonWithType
interface Property {
    val schema: DataSchema
}


/*
    注意，any、object 等也可以用这个属性，这个属性的意思是通用属性。
    是否需要区分 primitive 和 object、array，由 property 的使用方决定，比如 数据存储层、表现层。
    ObjectProperty、ArrayProperty则适用于当声明层需要分层属性时。
*/
data class SimpleProperty(
    override val schema: DataSchema
) : Property {
    @get:JsonIgnore
    val typeName: String
        get() {
            return when (schema) {
                is PrimitiveSchema -> schema.typeName()
                is ObjectSchema -> "object"
                is ArraySchema -> "array"
                else -> error("schema type name is not know")
            }
        }

    constructor(type: String) : this(PrimitiveSchema(listOf(NamedType(type))))
}

data class ObjectProperty(val properties: Map<Name, Property>) : Property {
    @get:JsonIgnore
    override val schema: DataSchema
        get() {
            return ObjectSchema(properties.mapValues { it.value.schema })
        }
}

data class ArrayProperty(val item: Property) : Property {
    @get:JsonIgnore
    override val schema: DataSchema
        get() {
            return ArraySchema(item.schema) //TODO 添加数组约束
        }
}

