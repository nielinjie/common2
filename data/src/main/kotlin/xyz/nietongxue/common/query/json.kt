package xyz.nietongxue.common.query

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.BooleanNode
import com.fasterxml.jackson.databind.node.IntNode
import com.fasterxml.jackson.databind.node.NumericNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.treeToValue
import xyz.nietongxue.common.base.stuff
import xyz.nietongxue.common.schema.CommonNamedTypes


/**
 *一种自然形式的简写。不是直接的 deserialize
 *```
 *   and: {
 *       name : {
 *           eq : "Alice"
 *       }
 *       age : {
 *           ge : 18, le : 21
 *       }
 *   }
 *```
 * */
typealias ValueAndTypeName = Pair<Any, String>


class DataStructureHint(val dataTypes: Map<String, String>) {
    fun getType(fieldName: String): String? {
        return dataTypes[fieldName]
    }
}

fun natureJsonToFilter(json: ArrayNode, dataStructureHint: DataStructureHint = DataStructureHint(emptyMap())): Filter {


    fun toValueType(json: JsonNode): ValueAndTypeName {
        return when (json) {
            is TextNode -> json.asText() to CommonNamedTypes.STRING.name
            is IntNode -> json.asInt() to CommonNamedTypes.INT.name
            is NumericNode -> json.asDouble() to CommonNamedTypes.NUMBER.name
            is BooleanNode -> json.asBoolean() to CommonNamedTypes.BOOLEAN.name
            is ArrayNode -> json.toList().map { toValueType(it)!!.first } to CommonNamedTypes.ARRAY.name
            else -> error("unsupported value type - ${json.nodeType}")
        }
    }

    fun withOperation(json: ObjectNode): List<Pair<Operator, ValueAndTypeName>> {
        return json.properties().map {
            when (it.key) {
                "eq" -> Operator.Equal to toValueType(it.value)
                "ne" -> Operator.NotEqual to toValueType(it.value)
                "gt" -> Operator.GreaterThan to toValueType(it.value)
                "lt" -> Operator.LessThan to toValueType(it.value)
                "ge" -> Operator.GreaterThanOrEqual to toValueType(it.value)
                "le" -> Operator.LessThanOrEqual to toValueType(it.value)
                "in" -> Operator.ContainedIn to toValueType(it.value)
                "like" -> Operator.Like to toValueType(it.value)
                "between" -> Operator.Between to toValueType(it.value)
                "isNull" -> Operator.IsNull to Pair(stuff(), CommonNamedTypes.OBJECT.name) //约定：以空对象为占位符。
                "isNotNull" -> Operator.IsNotNull to Pair(stuff(), CommonNamedTypes.OBJECT.name)
                else -> error("unsupported operator - ${it.key}")
            }.also {
                it.first.validateWithValueAndType(it.second)
            }
        }
    }

    fun withFieldName(json: ObjectNode): List<Pair<String, Pair<Operator, ValueAndTypeName>>> {
        return json.properties().flatMap { (fk, fv) ->
            runCatching { withOperation(fv as ObjectNode) }
                .map {
                    it.map { fk to it }
                }.getOrElse { e ->
                    throw IllegalArgumentException("处理字段 '$fk' 时发生错误：${e.message}", e)
                }
        }
    }


    fun toPiece(json: ObjectNode, pieceType: PieceType): List<FilterPiece> {
        return withFieldName(json).map {
            val (fieldName, operation) = it
            val (operator, valueAndType) = operation
            FilterPiece(
                pieceType = pieceType,
                fieldName = fieldName,
                operator = operator,
                value = valueAndType.first,
                fieldType = dataStructureHint.getType(fieldName),
                valueType = valueAndType.second
            )
        }
    }
    return json.flatMap {
        ((it as? ObjectNode) ?: error("unsupported expression format")).let {
            it.properties().flatMap {
                val pieceType = when (it.key) {
                    "and" -> PieceType.And
                    "or" -> PieceType.Or
                    "notAnd" -> PieceType.AndNot
                    "notOr" -> PieceType.OrNot
                    else -> error("unknown piece type - ${it.key}")
                }
                toPiece(it.value as ObjectNode, pieceType)
            }
        }
    }
}

/*


 */
fun natureJsonToQuery(
    json: ObjectNode,
    defaultPageSize: Int = 5,
    dataStructureHint: DataStructureHint = DataStructureHint(emptyMap())
): Query {
    val filter = (json.get("filter") as? ArrayNode)?.let {
        natureJsonToFilter(it, dataStructureHint)
    }

    fun objectToPiece(json: ObjectNode): List<SortPiece> {
        return json.properties().map {
            SortPiece(it.key, Direction.valueOf(it.value.asText().uppercase()))
        }
    }

    val sort: Sort? = ((json.get("sort") ?: json.get("order")) as? ArrayNode)?.let {
        it.mapNotNull { (it as? ObjectNode)?.let { objectToPiece(it) } }.flatten()
    } ?: ((json.get("sort") ?: json.get("order")) as? ObjectNode)?.let {
        objectToPiece(it)
    }
    val page = (json.get("page") as? ObjectNode)?.let {
        Paging(
            (it.get("pageIndex") ?: it.get("index"))?.asInt() ?: 0,
            (it.get("pageSize") ?: it.get("size"))?.asInt() ?: defaultPageSize
        )
    }
    return Query(filter, sort, page)
}

fun autoTryToQuery(
    json: ObjectNode,
    defaultPageSize: Int = 5,
    dataStructureHint: DataStructureHint = DataStructureHint(emptyMap())
): Query {
    val om = jacksonObjectMapper()
    return runCatching {
        om.treeToValue<Query>(json)
    }.getOrElse {
        natureJsonToQuery(json, defaultPageSize, dataStructureHint = dataStructureHint)
    }
}