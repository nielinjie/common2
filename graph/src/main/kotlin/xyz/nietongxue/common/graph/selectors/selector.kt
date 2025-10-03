package xyz.nietongxue.common.graph.selectors

import xyz.nietongxue.common.base.Id
import xyz.nietongxue.common.base.Name
import xyz.nietongxue.common.base.Selector
import xyz.nietongxue.common.graph.*

interface NodeSelector<V : Graph> : Selector<Node, V>
interface EdgeSelector<V : Graph> : Selector<Edge, V>


fun <G : BaseGraph> nodeById(id: Id): NodeSelector<G> = nodeSelector<Node, G> { it, _ ->
    it.id == id
}

inline fun <reified T, G : BaseGraph> nodeSelector(crossinline func: (T, G) -> Boolean): NodeSelector<G> {
    return object : NodeSelector<G> {
        override fun select(input: List<Node>, context: G): List<Node> {
            return input.filter {
                when (it) {
                    is T -> func(it, context)
                    else -> false
                }
            }
        }
    }
}

inline fun <reified T, G : BaseGraph> nodeSelector(crossinline func: (T) -> Boolean): NodeSelector<G> {
    return object : NodeSelector<G> {
        override fun select(input: List<Node>, context: G): List<Node> {
            return input.filter {
                when (it) {
                    is T -> func(it)
                    else -> false
                }
            }
        }
    }
}

inline fun <reified T, G : BaseGraph> edgeSelector(crossinline func: (T, G) -> Boolean): EdgeSelector<G> {
    return object : EdgeSelector<G> {
        override fun select(input: List<Edge>, context: G): List<Edge> {
            return input.filter {
                when (it) {
                    is T -> func(it, context)
                    else -> false
                }
            }
        }
    }
}

inline fun <reified T, G : BaseGraph> edgeSelector(crossinline func: (T) -> Boolean): EdgeSelector<G> {
    return object : EdgeSelector<G> {
        override fun select(input: List<Edge>, context: G): List<Edge> {
            return input.filter {
                when (it) {
                    is T -> func(it)
                    else -> false
                }
            }
        }
    }
}

fun <G : BaseGraph> edgeBySourceAndPort(nodeSelector: NodeSelector<G>, portName: Name): EdgeSelector<G> =
    object : EdgeSelector<G> {
        override fun select(input: List<Edge>, context: G): List<Edge> {
            val nodes = nodeSelector.select(context.nodes(), context)
            return input.filter { edge ->
                nodes.any { it.id == edge.from } && edge.on == portName
            }
        }
    }

fun <G : BaseGraph> edgeBySourceAndPortAndTarget(
    sourceNodeSelector: NodeSelector<G>, portName: Name, targetNodeSelector: NodeSelector<G>
): EdgeSelector<G> = object : EdgeSelector<G> {
    override fun select(input: List<Edge>, context: G): List<Edge> {
        val sourceNodes = sourceNodeSelector.select(context.nodes(), context)
        val targetNodes = targetNodeSelector.select(context.nodes(), context)
        return input.filter { edge ->
            sourceNodes.any { it.id == edge.from } && edge.on == portName && targetNodes.any { it.id == edge.to }
        }
    }
}

fun <G : BaseGraph> edgeByPortAndTarget(
    portName: Name, targetNodeSelector: NodeSelector<G>
): EdgeSelector<G> = object : EdgeSelector<G> {
    override fun select(input: List<Edge>, context: G): List<Edge> {
        val targetNodes = targetNodeSelector.select(context.nodes(), context)
        return input.filter { edge ->
            edge.on == portName && targetNodes.any { it.id == edge.to }
        }
    }
}

fun <G : BaseGraph> NodeSelector<G>.applyTo(graph: G): List<Node> {
    return this.select(graph.nodes(), graph)
}

fun <G : BaseGraph> EdgeSelector<G>.applyTo(graph: G): List<Edge> {
    return this.select(graph.edges(), graph)
}

fun <G : Graph, N : Node> Selector<N, G>.applyTo(graph: G): List<N> {
    return this.select(graph.nodes().mapNotNull { it as? N }, graph)
}