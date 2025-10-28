package xyz.nietongxue.common.diagram.platuml

import xyz.nietongxue.common.simpleGraph.Graph
import xyz.nietongxue.common.simpleGraph.Node
import xyz.nietongxue.common.base.v3
import xyz.nietongxue.common.simpleGraph.Edge

fun toGraph(uml: Uml): Graph {
    require(uml is Uml.Root)
    return toGraphImp(uml, Graph())
}

fun v3(name: String): String {
    return name.v3()
}

fun toGraphImp(uml: Uml, graph: Graph): Graph {
    return when (uml) {
        is Uml.Root -> {
            uml.units.fold(graph) { g, u ->
                toGraphImp(u, g)
            }
        }

        is Uml.Element -> {
            val node = Node(id = v3(uml.name), type = uml.type, stuff = mapOf("name" to uml.name))
            graph.copy(nodes = graph.nodes + node)
        }

        is Uml.Link -> {
            val edge = Edge(
                from = v3(uml.from), to = v3(uml.to),
                type = uml.type,
                stuff = mapOf()
            )
            graph.copy(edges = graph.edges + edge)
        }

        is Uml.Container -> {
            val node = Node(id = v3(uml.name), type = uml.type, stuff = mapOf("name" to uml.name))
            graph.copy(nodes = graph.nodes + node)
            uml.units.fold(graph) { g, u ->
                toGraphImp(u, g)
            }
        }
    }
}