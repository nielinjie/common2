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
    fun testInArray() {
        val simpleFilter = """
            [{and: {foo.bar : {in: [Alice]}}}] 
        """.trimIndent()
        val filterJson = autoParse(simpleFilter)
        val filter = natureJsonToFilter(filterJson.first as ArrayNode)
        pretty(filter)
    }


    @Test
    fun testMorePieceMode() {
        val simpleFilter = """
            [{and: {name: {eq: "Alice"}, age : {ge: 18, le: 21}}}] 
        """.trimIndent()
        val filterJson = autoParse(simpleFilter)
        val filter = natureJsonToFilter(filterJson.first as ArrayNode)
        pretty(filter)
    }

    @Test
    fun testException() {
        val simpleFilter = """
            [{and: {name: {edq: "Alice"}, age : {ge: 18, le: 21}}}] 
        """.trimIndent()
        val filterJson = autoParse(simpleFilter)

        val filter = runCatching { natureJsonToFilter(filterJson.first as ArrayNode) }.getOrElse(
            {
                println(it)
            }
        )
    }

    @Test
    fun testException2() {
        val simpleFilter = """
            [[{and: {name: {edq: "Alice"}, age : {ge: 18, le: 21}}}]] 
        """.trimIndent()
        val filterJson = autoParse(simpleFilter)

        val filter = runCatching { natureJsonToFilter(filterJson.first as ArrayNode) }.getOrElse(
            {
                println(it)
            }
        )
    }

    @Test
    fun testException3() {
        val simpleFilter = """
            [{and: {name: {like: 32}, age : {ge: 18, le: 21}}}] 
        """.trimIndent()
        val filterJson = autoParse(simpleFilter)

        val filter = runCatching { natureJsonToFilter(filterJson.first as ArrayNode) }.getOrElse(
            {
                println(it)
            }
        )
    }


}