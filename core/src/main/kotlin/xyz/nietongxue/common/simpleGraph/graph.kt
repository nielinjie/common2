package xyz.nietongxue.common.simpleGraph

import xyz.nietongxue.common.base.Id
import xyz.nietongxue.common.base.Stuff
import xyz.nietongxue.common.base.v7

data class Graph(
    val nodes: List<Node> = emptyList(),
    val edges: List<Edge> = emptyList()
) {
    fun findPath(from: Id, to: Id): List<String> {

        val adjacency = edges.groupBy { it.from }
        val queue = ArrayDeque<List<Id>>()
        queue.add(listOf(from))
        val visited = mutableSetOf<Id>()
        visited.add(from)

        while (queue.isNotEmpty()) {
            val currentPath = queue.removeFirst()
            val current = currentPath.last()
            val outgoingEdges = adjacency[current] ?: emptyList()

            for (edge in outgoingEdges) {
                val next = edge.to
                if (next !in visited) {
                    val newPath = currentPath + next
                    if (next == to) return newPath
                    visited.add(next)
                    queue.add(newPath)
                }
            }
        }

        return emptyList()
    }

    fun hasPath(from: Id, to: Id): Boolean {
        return findPath(from, to).isNotEmpty()
    }

    fun findPathAsEdges(from: Id, to: Id): List<Edge> {
        val path = findPath(from, to)
        return path.zipWithNext { a, b ->
            edges.first { it.from == a && it.to == b }
        }
    }

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