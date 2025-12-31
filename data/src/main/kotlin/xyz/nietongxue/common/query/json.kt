package xyz.nietongxue.common.query

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.BooleanNode
import com.fasterxml.jackson.databind.node.IntNode
import com.fasterxml.jackson.databind.node.NumericNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode


/*
一种自然形式的简写。不是直接的 deserialize
 */
/*
    and: {
        name : {
            eq : "Alice"
        }
    }
    */

fun natureJsonToFilter(json: ArrayNode): Filter {

    fun toValueType(json: JsonNode): Pair<Any, String>? {
        return when (json) {
            is TextNode -> json.asText() to "string"
            is IntNode -> json.asInt() to "number"
            is NumericNode -> json.asDouble() to "number"
            is BooleanNode -> json.asBoolean() to "boolean"
            else -> null
        }
    }

    fun toOperation(json: ObjectNode): Pair<Operator, Pair<Any, String>>? {
        return json.properties().singleOrNull()?.let {
            when (it.key) {
                "eq" -> Operator.Equal to (toValueType(it.value) ?: error("unknown value and type"))
                "ne" -> Operator.NotEqual to (toValueType(it.value) ?: error("unknown value and type"))
                "gt" -> Operator.GreaterThan to (toValueType(it.value) ?: error("unknown value and type"))
                "lt" -> Operator.LessThan to (toValueType(it.value) ?: error("unknown value and type"))
                else -> null
            }
        }
    }

    fun toFieldName(json: ObjectNode): Pair<String, Pair<Operator, Pair<Any, String>>>? {
        return json.properties().singleOrNull()?.let {
            return it.key to (toOperation(it.value as ObjectNode) ?: error("unknown operation and value"))
        }
    }
    return json.mapNotNull {
        (it as? ObjectNode)?.let {
            it.properties().singleOrNull()?.let {
                val pieceType = when (it.key) {
                    "and" -> PieceType.And
                    "or" -> PieceType.Or
                    "notAnd" -> PieceType.AndNot
                    "notOr" -> PieceType.OrNot
                    else -> error("unknown piece type")
                }
                val (fieldName, operation) = toFieldName(it.value as ObjectNode) ?: error("unknown field name")
                val (operator, valueAndType) = operation
                val trySplit = fieldName.split(".", limit = 2)
                if (trySplit.size == 2)
                    FilterPiece(
                        pieceType = pieceType,
                        fieldName = trySplit[1],
                        operator = operator,
                        value = valueAndType.first,
                        type = valueAndType.second,
                        entityName = trySplit[0]
                    )
                else
                    FilterPiece(
                        pieceType = pieceType,
                        fieldName = fieldName,
                        operator = operator,
                        value = valueAndType.first,
                        type = valueAndType.second
                    )
            }
        }
    }
}


fun natureJsonToQuery(json: ObjectNode): Query {
    val filter = (json.get("filter") as? ArrayNode)?.let {
        natureJsonToFilter(it)
    }
    val sort: Sort? = (json.get("sort") as? ObjectNode)?.let {
        it.properties().map {
            SortPiece(it.key, Direction.valueOf(it.value.asText().uppercase()))
        }

    }
    val page = (json.get("page") as? ObjectNode)?.let {
        Paging(
            (it.get("pageIndex") ?: it.get("index"))?.asInt() ?: 0,
            (it.get("pageSize") ?: it.get("size"))?.asInt() ?: 100
        )
    }
    return Query(filter, sort, page)
}