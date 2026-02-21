package xyz.nietongxue.common.query


interface Fields<T :Any> {
    fun getFieldNames(): List<String>
    fun getFieldType(fieldName: String): String
    fun get(obj: T, fieldName: String): Any?
}

class  Filtering<T :Any>(val filter: Filter, val fieldsType: Fields<T>) {
    fun filter(objs: List<T>): List<T> {
        return objs.filter {
            this.match(it)
        }
    }

    fun match(obj: T): Boolean {
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

    fun match(obj: T, filterPiece: FilterPiece): Boolean {
        val fieldValue = fieldsType.get(obj, filterPiece.fieldName)
        val fieldType = fieldsType.getFieldType(filterPiece.fieldName)
        return when (filterPiece.operator) {
            Operator.Equal -> fieldValue == filterPiece.value
            Operator.NotEqual -> fieldValue != filterPiece.value
            else -> TODO()
        }
    }
}