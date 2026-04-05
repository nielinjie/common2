package xyz.nietongxue.common.query

interface Fields {
    fun get(obj: Any, fieldName: String): Any?
}

open class Filtering(val filter: Filter, val fields: Fields) {
    open fun filter(objs: List<Any>): List<Any> {
        return objs.filter {
            this.match(it)
        }
    }

    fun match(obj: Any): Boolean {
        if (filter.isEmpty()) return true
        val booleans = filter.map {
            match(obj, it)
        }
        return booleans.withIndex().fold(true) { acc, value ->
            when (filter[value.index].pieceType) {
                PieceType.And -> acc && value.value
                PieceType.Or -> acc || value.value
                else -> error("not supported piece type")
            }
        }
    }

    fun match(obj: Any, filterPiece: FilterPiece): Boolean {
        val fieldValue = fields.get(obj, filterPiece.fieldName)
        return when (filterPiece.operator) {
            Operator.Equal -> fieldValue == filterPiece.value
            Operator.NotEqual -> fieldValue != filterPiece.value
            else -> TODO()
        }
    }
}