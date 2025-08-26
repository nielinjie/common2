package xyz.nietongxue.common.schema

import com.fasterxml.jackson.annotation.JsonTypeInfo
import xyz.nietongxue.common.base.cList
import xyz.nietongxue.common.schema.json.Format


@JsonTypeInfo(
    use = JsonTypeInfo.Id.CLASS,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
interface DataSchema

data class PrimitiveSchema(val constraints: List<Constraint>) : DataSchema {
    fun typeName(): String {
        return constraints.filterIsInstance<NamedType>().first().name
    }

    companion object {
        fun fromString(textValue: String, format: Format): PrimitiveSchema? {
            val recognizers: List<ConstraintRecognizer> = listOf(
                TypeNameRecognizer, RequiredRecognizer
            )
            val list = format.shortList(textValue)
            val constraints = list.mapNotNull { text ->
                recognizers.firstNotNullOfOrNull { it.recognize(text) }
            }
            return PrimitiveSchema(constraints)
        }
    }
}


//限制

@JsonTypeInfo(
    use = JsonTypeInfo.Id.CLASS,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
interface Constraint

data class NamedType(val name: String) : Constraint
data object Required : Constraint
data object NotEmpty : Constraint
data class LengthRange(val min: Int?, val max: Int?) : Constraint {
    init {
        require(min != null || max != null)
    }
}

data class Range(val min: Int?, val max: Int?) : Constraint {
    init {
        require(min != null || max != null)
    }
}

data class Pattern(val regex: String) : Constraint

interface ConstraintRecognizer {
    fun recognize(textValue: String): Constraint?
}

object TypeNameRecognizer : ConstraintRecognizer {
    override fun recognize(textValue: String): Constraint? {
        val types = "string,number,boolean,any".cList()
        return types.firstOrNull {
            textValue == it
        }?.let {
            NamedType(textValue)
        }
    }
}

object RequiredRecognizer : ConstraintRecognizer {
    override fun recognize(textValue: String): Constraint? {
        return if (textValue.lowercase() == "required"
            || textValue.lowercase() == "notNull".lowercase()
        ) {
            return Required
        } else {
            null
        }
    }

}


data class ArraySchema(val itemSchema: DataSchema) : DataSchema
data class ObjectSchema(val properties: Map<String, DataSchema>) : DataSchema {
    fun additional(): AdditionalProperties? {
        return this.properties.filter {
            it.key == "_"
        }.firstNotNullOfOrNull {
            AdditionalProperties(it.value)
        }
    }
}

data class AdditionalProperties(val schema: DataSchema)



