package xyz.nietongxue.common.sql

import arrow.core.tail
import xyz.nietongxue.common.query.Direction
import xyz.nietongxue.common.query.Filter
import xyz.nietongxue.common.query.FilterPiece
import xyz.nietongxue.common.query.Operator
import xyz.nietongxue.common.query.Paging
import xyz.nietongxue.common.query.PieceType
import xyz.nietongxue.common.query.Query
import xyz.nietongxue.common.query.Sort
import xyz.nietongxue.common.schema.CommonNamedTypes
import xyz.nietongxue.common.string.escape
import xyz.nietongxue.common.string.wrapBy
import xyz.nietongxue.common.string.replacePlaceholders


typealias SqlStatement = String

data class QuerySqlStatements(
    val where: SqlStatement,
    val order: SqlStatement,
    val page: SqlStatement
)

object QueryToSql {
    fun filterPieceCondition(piece: FilterPiece): String =
        piece.fieldName.let {
            val rhs: String = when (piece.type) {
                CommonNamedTypes.STRING.name -> (piece.value.toString()).escape("sql").wrapBy("'") //TODO common type names
//                CommonNamedTypes.TIMESTAMP.name -> piece.value.toString()
                else -> piece.value.toString()
            }
            val oper = when (piece.operator) {
                Operator.Equal -> " = "
                Operator.GreaterThan -> " > "
                Operator.LessThan -> " < "
                Operator.NotEqual -> " != "
                Operator.ContainedIn -> " in "
                Operator.Like -> " like "
                else -> error("Invalid operator")
            }
            return "$it $oper $rhs"
        }

    fun filterToCondition(filter: List<FilterPiece>): String {
        var condition = filterPieceCondition(filter.first())
        for (piece in filter.tail()) {
            condition = condition + when (piece.pieceType) {
                PieceType.And -> " and "
                PieceType.Or -> " or "
                else -> error("Invalid filter")
            } + filterPieceCondition(piece)

        }
        return condition
    }

    fun toSql(query: Query): QuerySqlStatements {
        fun where(filter: Filter): String = " where " + filterToCondition(filter)
        fun order(sort: Sort): String = " order by " + sort.joinToString(", ") {
            it.fieldName + " " + when (it.direction) {
                Direction.ASC -> "ASC"
                Direction.DESC -> "DESC"
            }
        }

        fun page(paging: Paging): String = " limit " + paging.pageSize + " offset " + paging.pageIndex * paging.pageSize
        return QuerySqlStatements(
            query.filter?.let { if (it.isEmpty()) "" else where(it) } ?: "",
            query.sort?.let(::order) ?: "",
            query.paging?.let(::page) ?: ""
        )
    }

}

fun injectToSql(statements: QuerySqlStatements, sql: String): String {
    return replacePlaceholders(
        sql, mapOf(
            "where" to statements.where,
            "order" to statements.order,
            "page" to statements.page
        )
    )
}