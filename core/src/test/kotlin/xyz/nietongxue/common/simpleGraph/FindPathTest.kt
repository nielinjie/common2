package xyz.nietongxue.common.simpleGraph

import org.junit.jupiter.api.Test
import xyz.nietongxue.common.base.stuff


fun node(id: String) = Node(
    id = id,
    type = "node",
    stuff = stuff()
)

fun edge(from: String, to: String) = Edge(
    id = "$from-$to",
    type = "edge",
    from = from,
    to = to,
    stuff = stuff()
)

class FindPathTest {

    @Test
    fun test() {
        val graph = Graph(
            nodes = listOf(
                node("A"), node("B"),
                node("C"), node("D")
            ), edges = listOf(
                edge("A", "B"),
                edge("A", "C"),
                edge("B", "D"),
                edge("C", "D")
            )
        )
        println(graph.findPath("A", "D"))
        println(graph.findPathAsEdges("A", "D"))
    }

    @Test
    fun test2() {
        val graph = Graph(
            nodes = listOf(
                node("A"), node("B"),
                node("C"), node("D")
            ), edges = listOf(
                edge("A", "B"),
                edge("A", "C"),
//                edge("B","D"),
                edge("C", "D")
            )
        )
        println(graph.findPath("A", "D"))
        println(graph.findPathAsEdges("A", "D"))

    }

    @Test
    fun test3() {
        val graph = Graph(
            nodes = listOf(
                node("A"), node("B"),
                node("C"), node("D")
            ), edges = listOf(
                edge("A", "B"),
//                edge("A","C"),
//                edge("B","D"),
                edge("C", "D")
            )
        )
        println(graph.findPath("A", "D"))
        println(graph.findPathAsEdges("A", "D"))

    }
}