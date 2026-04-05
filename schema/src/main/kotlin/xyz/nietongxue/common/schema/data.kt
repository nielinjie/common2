package xyz.nietongxue.common.schema

import xyz.nietongxue.common.json.JsonWithType
import xyz.nietongxue.common.json.autoParse.Format


annotation class Schema(
    val name: String,
    val description: String,
    val dataSchema: String,
)

@JsonWithType
interface DataSchema

//主要用在 json schema 里面。一般的 schema 用 primary
data class BooleanSchema(val value: Boolean) : DataSchema


data class PrimitiveSchema(val constraints: List<Constraint>, val description: String? = null) : DataSchema {
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

typealias Constraints = List<Constraint>

fun Constraints.isRequired(): Boolean {
    return this.contains(Required)
}

fun DataSchema.thisConstraints(): Constraints {
    return when (this) {
        is PrimitiveSchema -> this.constraints
        is ObjectSchema -> this.objectConstraint
        is ArraySchema -> this.arrayConstraints
        else -> error("not support this type")
    }
}

data class NamedType(val name: String) : Constraint
data object Required : Constraint
data object NotEmpty : Constraint
data class LengthRange(val min: Int?, val max: Int?) : Constraint {
    init {
        require(min != null || max != null)
    }
}


object Schemas {
    fun string(): PrimitiveSchema {
        return PrimitiveSchema(listOf(NamedType(CommonNamedTypes.STRING.name)))
    }

    fun number(): PrimitiveSchema {
        return PrimitiveSchema(listOf(NamedType(CommonNamedTypes.NUMBER.name)))
    }

    fun boolean(): PrimitiveSchema {
        return PrimitiveSchema(listOf(NamedType(CommonNamedTypes.BOOLEAN.name)))
    }

    fun int(): PrimitiveSchema {
        return PrimitiveSchema(listOf(NamedType(CommonNamedTypes.INT.name)))
    }

}


fun DataSchema.required(): DataSchema {
    return when (this) {
        is PrimitiveSchema -> this.copy(constraints = this.constraints + Required)
        is ObjectSchema -> this.copy(objectConstraint = this.objectConstraint + Required)
        is ArraySchema -> this.copy(arrayConstraints = this.arrayConstraints + Required)
        else -> this
    }
}

fun DataSchema.desc(description: String): DataSchema {
    return when (this) {
        is PrimitiveSchema -> this.copy(description = description)
        is ObjectSchema -> this.copy(description = description)
        is ArraySchema -> this.copy(description = description)
        else -> this
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


data class ArraySchema(
    val itemSchema: DataSchema,
    val arrayConstraints: List<Constraint> = listOf(),
    val description: String? = null
) : DataSchema

data class ObjectSchema(
    val properties: Map<String, DataSchema>,
    val objectConstraint: List<Constraint> = listOf(),
    val description: String? = null,
    val additionalProperties: AdditionalProperties? = null //TODO 如何表现： additional properties is allowed and can be any.
) : DataSchema

data class AdditionalProperties(val schema: DataSchema)


fun PrimitiveSchema.maxLength(): Int? {
    return this.constraints.filterIsInstance<LengthRange>().firstOrNull()?.max
}
