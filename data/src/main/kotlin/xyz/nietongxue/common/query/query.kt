package xyz.nietongxue.common.query

data class Query(
    val filter: Filter?,
    val sort: Sort?,
    val paging: Paging?,
)

fun Query.addFilter(filter: Filter): Query {
    return this.copy(filter = (this.filter ?: emptyList()).plus(filter))
}
typealias Sort = List<SortPiece>

data class SortPiece(
    val fieldName: String,
    val direction: Direction,
    val entityName: String? = null
)

enum class Direction {
    ASC,
    DESC
}

data class Paging(
    val pageIndex: Int,
    val pageSize: Int
)

data class Paged(
    val totalItems: Int,
    val pageNumber: Int
)

