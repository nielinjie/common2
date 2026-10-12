package xyz.nietongxue.common.schema.openApi

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import xyz.nietongxue.common.base.Stuff
import xyz.nietongxue.common.schema.jsonSchema.schemaFromClass
import xyz.nietongxue.common.schema.jsonSchema.toFullJsonSchema
import kotlin.test.Test


data class QueryRequest(val filter: Stuff, val contentQuery: ContentQuery)


@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = ContentQuery.ByKeyword::class, name = "byKeyword"),
    JsonSubTypes.Type(value = ContentQuery.BySemantic::class, name = "bySemantic")
)
interface ContentQuery {
    data class ByKeyword(val keywords: List<String>) : ContentQuery
    data class BySemantic(val words: String) : ContentQuery //Semantic similarity，通常通过向量距离
}

class SubTypesTest {


    @Test
    fun testCreateJsonSchema() {
        val resolvedSchema = schemaFromClass(ContentQuery::class.java)
        val fullJsonSchema = toFullJsonSchema(resolvedSchema)
        println(fullJsonSchema.toPrettyString())
    }
    @Test
    fun testCreateJsonSchema2() {
        val resolvedSchema = schemaFromClass(QueryRequest::class.java)
        val fullJsonSchema = toFullJsonSchema(resolvedSchema)
        println(fullJsonSchema.toPrettyString())
    }


}
