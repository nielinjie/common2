package xyz.nietongxue.common.query

import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import xyz.nietongxue.common.collections.toJsonStringForAny
import xyz.nietongxue.common.json.autoParse.autoParse
import xyz.nietongxue.common.testing.pretty
import kotlin.test.Test

class FilterTest {

    @Test
    fun test() {
        val filter = listOf(
            FilterPiece(
                fieldName = "name",
                operator = Operator.Equal,
                value = "Alice",
                pieceType = PieceType.And
            ),
            FilterPiece(
                fieldName = "age",
                operator = Operator.Equal,
                value = "30",
                pieceType = PieceType.And
            )
        )
        val string = filter.toJsonStringForAny().also {
            println(it)
        }
        val nature = filter.toNatureString().also {
            println(it)
        }
    }

    @Test
    fun testEntityName() {
        val simpleFilter = """
            [{and: {foo.bar : {eq: "Alice"}}}] 
        """.trimIndent()
        val filterJson = autoParse(simpleFilter)
        val filter = natureJsonToFilter(filterJson.first as ArrayNode)
        pretty(filter)
    }


    @Test
    fun testSort() {
        val sort = """
            {sort:{name: asc}}
        """.trimIndent()
        val result = autoParse(sort).let {
            natureJsonToQuery(it.first as ObjectNode)
        }
        println(result)
    }
    @Test
    fun testSort2() {
        val sort = """
            {order:{name: asc,age: desc}}
        """.trimIndent()
        val result = autoParse(sort).let {
            natureJsonToQuery(it.first as ObjectNode)
        }
        println(result)
    }
    @Test
    fun testSort3() {
        val sort = """
            {order:[{name: asc},{age: desc}]}
        """.trimIndent()
        val result = autoParse(sort).let {
            natureJsonToQuery(it.first as ObjectNode)
        }
        println(result)
    }
}