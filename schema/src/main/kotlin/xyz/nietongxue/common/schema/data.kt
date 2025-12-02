package xyz.nietongxue.common.schema

import com.fasterxml.jackson.annotation.JsonTypeInfo
import xyz.nietongxue.common.json.JsonWithType
import xyz.nietongxue.common.json.autoParse.Format


@JsonWithType
interface DataSchema

//主要用在 json schema 里面。一般的 schema 用 primary？
data class BooleanSchema(val value: Boolean) : DataSchema


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

@JsonWithType
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

/*
 */

object TypeNameRecognizer : ConstraintRecognizer {
    override fun recognize(textValue: String): Constraint? {
        val types = listOf(
            CommonNamedTypes.STRING,
            CommonNamedTypes.NUMBER, CommonNamedTypes.BOOLEAN, CommonNamedTypes.ANY,
            CommonNamedTypes.INT, CommonNamedTypes.LONG, CommonNamedTypes.UUID,
            CommonNamedTypes.DATETIME, CommonNamedTypes.TIMESTAMP
        ).map { it.name }
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
            || textValue.equals("notNull", ignoreCase = true)
        ) {
            return Required
        } else {
            null
        }
    }

}


data class ArraySchema(val itemSchema: DataSchema, val arrayConstraints: List<Constraint> = listOf()) : DataSchema
data class ObjectSchema(val properties: Map<String, DataSchema>) : DataSchema {
    fun additional(): AdditionalProperties {
        return this.properties["_"]?.let {
            AdditionalProperties(it)
        } ?: AdditionalProperties(BooleanSchema(true))
    }
}

data class AdditionalProperties(val schema: DataSchema)


fun PrimitiveSchema.maxLength(): Int? {
    return this.constraints.filterIsInstance<LengthRange>().firstOrNull()?.max
}

fun PrimitiveSchema.required(): Boolean {
    return this.constraints.contains(Required)
}