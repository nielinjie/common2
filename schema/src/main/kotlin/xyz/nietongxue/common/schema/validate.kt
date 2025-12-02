package xyz.nietongxue.common.schema

import arrow.core.Either
import arrow.core.Nel
import arrow.core.toNonEmptyListOrNull
import xyz.nietongxue.common.log.Log
import xyz.nietongxue.common.validate.error as er

typealias ValidateResult = Either<Nel<ValidateResultItem>, Any?>
typealias ValidateResultItem = Log<String>

//validate, 不是通过 jsonschema，自己 validate.

fun validateWithData(data: Any?, schema: DataSchema): ValidateResult {
    return validate(data, schema).let {
        if (it.isNotEmpty()) {
            Either.Left(it.toNonEmptyListOrNull()!!)
        } else Either.Right(data)
    }
}

fun validate(data: Any?, schema: DataSchema): List<ValidateResultItem> {
    return when (schema) {
        is PrimitiveSchema -> {
            schema.constraints.map {
                validate(data, it)
            }.flatten()
        }

        is ObjectSchema -> {
            when (data) {
                is Map<*, *> -> {
                    data.map {
                        val property = schema.properties[it.key]
                        if (property == null) {
                            listOf(er("property not found"))
                        } else {
                            validate(it.value, property)
                        }
                    }.flatten()
                }

                else -> {
                    listOf(er("object expected"))
                }
            }
        }

        is ArraySchema -> {
            when (data) {
                is List<*> -> {
                    validate(data, schema.arrayConstraints) +
                            data.map { item ->
                                validate(item, schema.itemSchema)
                            }.flatten()
                }

                else -> {
                    listOf(er("array expected"))
                }
            }
        }

        else -> error("not support schema")
    }
}

fun validate(data: Any?, constraints: List<Constraint>): List<ValidateResultItem> {
    return constraints.map {
        validate(data, it)
    }.flatten()
}


fun validate(data: Any?, constraint: Constraint): List<ValidateResultItem> {
    return when (constraint) {
        is Required -> {
            if (data == null) {
                listOf(er("required"))
            } else emptyList()
        }

        is NotEmpty -> {
            when (data) {
                is String -> {
                    if (data.isEmpty()) {
                        listOf(er("not empty"))
                    } else emptyList()
                }

                is Collection<*> -> {
                    if (data.isEmpty()) {
                        listOf(er("not empty"))
                    } else emptyList()
                }

                is Array<*> -> {
                    if (data.isEmpty()) {
                        listOf(er("not empty"))
                    } else emptyList()
                }

                else -> error("not supported data type")
            }
        }

        is LengthRange -> {
            when (data) {
                is String -> {
                    if (constraint.min != null && data.length < constraint.min
                        || constraint.max != null && data.length > constraint.max
                    ) {
                        listOf(er("length range"))
                    } else emptyList()
                }

                is Collection<*> -> {
                    if (constraint.min != null && data.size < constraint.min
                        || constraint.max != null && data.size > constraint.max
                    ) {
                        listOf(er("length range"))
                    } else emptyList()
                }

                is Array<*> -> {
                    if (constraint.min != null && data.size < constraint.min
                        || constraint.max != null && data.size > constraint.max
                    ) {
                        listOf(er("length range"))
                    } else emptyList()
                }

                else -> error("not supported data type")
            }
        }

        is Range -> {
            when (data) {
                is Number -> {
                    if (constraint.min != null && data.toDouble() < constraint.min
                        || constraint.max != null && data.toDouble() > constraint.max
                    ) {
                        listOf(er("range"))
                    } else emptyList()
                }

                else -> emptyList()
            }
        }

        is NamedType -> {
            when (constraint.name) {
                "string" -> if (data is String) emptyList() else listOf(er("type"))
                "number" -> if (data is Number) emptyList() else listOf(er("type"))
                "boolean" -> if (data is Boolean) emptyList() else listOf(er("type"))
                else -> emptyList() //TODO add more
            }
        }

        else -> emptyList() //error("not support constraint")
    }
}