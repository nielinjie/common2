package xyz.nietongxue.common.query

import com.fasterxml.jackson.databind.JsonNode
import xyz.nietongxue.common.base.Stuff
import xyz.nietongxue.common.collections.toJsonString
import xyz.nietongxue.common.json.JsonWithType
import xyz.nietongxue.common.json.defaultOM
import xyz.nietongxue.common.json.ja
import xyz.nietongxue.common.json.jo
import xyz.nietongxue.common.schema.CommonNamedTypes


/*
0. nature 形式：这个形式跟 array 形式没啥区别好像。
    [
        and: {
            name : {
                eq : "Alice"
            }
        }
    ]
0.5 object 形式：
    [
        {pieceType: and, fieldName: name, operator: eq, value: Alice}
    ]
1. array 形式：
    [[and, name, eq, Alice],[and , age, gt, 18, number]]
2. cypher 形式
    true and name = 'Alice' and age > 18

 */

typealias Filter = List<FilterPiece>

data class FilterPiece(
    val fieldName: String,
    val operator: Operator,
    val value: Any,
    val valueType: String = CommonNamedTypes.STRING.name, //TODO common type names
    val fieldType: String? = null,
    val pieceType: PieceType,
//    val entityName: String? = null
) {
    init {
        //TODO 增加一些验证，比如 operator、fieldType、valueType、value的适配性。

    }
}

@JsonWithType
interface Operator {
    data object Equal : Operator
    data object NotEqual : Operator
    data object GreaterThan : Operator
    data object LessThan : Operator
    data object GreaterThanOrEqual : Operator
    data object LessThanOrEqual : Operator
    data object ContainedIn : Operator
    data object Like : Operator
    data object Between : Operator
    data object IsNull : Operator
    data object IsNotNull : Operator

    fun validateWithValueAndType(valueAndTypeName: ValueAndTypeName) {
        when (this) {
            ContainedIn -> require(
                valueAndTypeName.second in listOf(
                    CommonNamedTypes.STRING.name,
                    CommonNamedTypes.ARRAY.name
                )
            ) {
                "operator containedIn value must be string or array, but ${valueAndTypeName.second} got"
            }

            Like -> require(valueAndTypeName.second == CommonNamedTypes.STRING.name) {
                "operator like value must be string, but ${valueAndTypeName.second} got"
            }

            Between -> require(valueAndTypeName.second == CommonNamedTypes.ARRAY.name && (valueAndTypeName.first as List<*>).size == 2) {
                "operator between value must be array, and it size is 2, but ${valueAndTypeName.second} got"
            }

        }
    }
}

fun Operator.toNatureString(): String {
    return when (this) {
        Operator.Equal -> "=="
        Operator.NotEqual -> "!="
        Operator.GreaterThan -> ">"
        Operator.LessThan -> "<"
        Operator.GreaterThanOrEqual -> ">="
        Operator.LessThanOrEqual -> "<="
        Operator.ContainedIn -> "in"
        Operator.Like -> "like"
        Operator.Between -> "between"
        Operator.IsNull -> "isNull"
        Operator.IsNotNull -> "isNotNull"
        else -> error("unknown operator")
    }
}


fun Operator.toShortString(): String {
    return when (this) {
        Operator.Equal -> "eq"
        Operator.NotEqual -> "ne"
        Operator.GreaterThan -> "gt"
        Operator.LessThan -> "lt"
        Operator.GreaterThanOrEqual -> "ge"
        Operator.LessThanOrEqual -> "lt"
        Operator.ContainedIn -> "in"
        Operator.Like -> "like"
        Operator.Between -> "between"
        Operator.IsNull -> "isNull"
        Operator.IsNotNull -> "isNotNull"
        else -> error("unknown operator")
    }
}

@JsonWithType
interface PieceType {
    data object And : PieceType
    data object Or : PieceType
    data object AndNot : PieceType
    data object OrNot : PieceType
}

fun PieceType.prefix(): String = when (this) {
    PieceType.And -> "true"
    PieceType.Or -> "false"
    else -> error("not support piece type")
}

fun PieceType.joiningString(): String = when (this) {
    PieceType.And -> "and"
    PieceType.Or -> "or"
    else -> error("not support piece type")
}

/**
 * 返回一个人类易读的字符串
 * 注意：不是一个可以用来解析的自然 json
 */
fun Filter.toNatureString(): String {
    val start = this.first().pieceType.prefix()
    return "$start " + this.joinToString(" ") {
        it.pieceType.joiningString() + " " + it.fieldName + " " + it.operator.toNatureString() + " " + it.value
    }
}

/**
 * 返回一个可以用来解析的自然 json
 */
fun Filter.toNatureJson(): JsonNode {
    return ja(
        *this@toNatureJson.map {
            jo(
                it.pieceType.joiningString() to jo(
                    it.fieldName to jo(
                        it.operator.toShortString() to defaultOM.valueToTree(it.value)
                    )
                )
            )
        }.toTypedArray()
    )

}


fun Stuff.asFilter(): Filter {
    return this.map {
        FilterPiece(
            fieldName = it.key,
            operator = Operator.Equal,
            value = it.value,
            pieceType = PieceType.And
        )
    }
}

fun Filter.and(other: Filter): Filter {
    require(this.all { it.pieceType == PieceType.And } && other.all { it.pieceType == PieceType.And }) {
        "相当于 and 后面加括号里面可能有 or，解开括号是否是等于 and and and and"
    }
    return this + other.map {
        it.copy(pieceType = PieceType.And)
    }
}



