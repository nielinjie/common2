package xyz.nietongxue.common.query

import com.fasterxml.jackson.databind.node.ArrayNode
import xyz.nietongxue.common.json.autoParse.autoParse
import xyz.nietongxue.common.testing.pretty
import kotlin.test.Test

class FilterWithHint {
    @Test
    fun testMorePieceMode() {
        val simpleFilter = """
            [{and: {name: {eq: "Alice"}, age : {ge: 18, le: 21}}}] 
        """.trimIndent()
        val filterJson = autoParse(simpleFilter)
        val hint = DataStructureHint(
            mapOf(
                "name" to "string",
                "age" to "int"
            )
        )
        val filter = natureJsonToFilter(filterJson.first as ArrayNode, hint)
        pretty(filter)
    }
}