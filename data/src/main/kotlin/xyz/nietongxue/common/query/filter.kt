package xyz.nietongxue.common.query

import xyz.nietongxue.common.json.JsonWithType
import xyz.nietongxue.common.schema.CommonNamedTypes


/*
0. json 形式：这个形式跟 array 形式没啥区别好像。
    [
        and: {
            name : {
                eq : "Alice"
            }
        }
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
    val type: String = CommonNamedTypes.STRING.name, //TODO common type names
    val pieceType: PieceType,
    val entityName: String? = null
)

@JsonWithType
interface Operator {
    data object Equal : Operator
    data object NotEqual : Operator
    data object GreaterThan : Operator
    data object LessThan : Operator
    data object ContainedIn : Operator
    data object Like : Operator
}

fun Operator.toNatureString(): String {
    return when (this) {
        Operator.Equal -> "=="
        Operator.NotEqual -> "!="
        Operator.GreaterThan -> ">"
        Operator.LessThan -> "<"
        Operator.ContainedIn -> "in"
        Operator.Like -> "like"
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

fun Filter.toNatureString(): String {
    val start = this.first().pieceType.prefix()
    return "$start " + this.joinToString(" ") {
        it.pieceType.joiningString() + " " + it.fieldName + " " + it.operator.toNatureString() + " " + it.value
    }
}





