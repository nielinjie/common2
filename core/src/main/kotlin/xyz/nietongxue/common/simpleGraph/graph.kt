package xyz.nietongxue.common.simpleGraph

import xyz.nietongxue.common.base.Id
import xyz.nietongxue.common.base.Name
import xyz.nietongxue.common.base.Stuff
import xyz.nietongxue.common.base.v7

data class Graph(
    val nodes: List<Node> = emptyList(),
    val edges: List<Edge> = emptyList()
) {

}

data class Node(
    val id: Id = v7(),
    val type: String,
    val stuff: Stuff
) {

}

data class Edge(
    val id: Id = v7(),
    val from: Id,
    val to: Id,
    val type: String,
    val stuff: Stuff
)