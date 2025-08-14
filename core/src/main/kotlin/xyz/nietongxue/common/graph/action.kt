package xyz.nietongxue.common.graph

import xyz.nietongxue.common.base.Record
import xyz.nietongxue.common.graph.selectors.NodeSelector
import xyz.nietongxue.common.processing.Action
import xyz.nietongxue.common.processing.ProcessingWithLog
import xyz.nietongxue.common.processing.Reducer
import xyz.nietongxue.common.processing.justError
import xyz.nietongxue.common.processing.resultAndLog
import xyz.nietongxue.common.processing.withLog
import xyz.nietongxue.common.processing.StopResult
import xyz.nietongxue.common.processing.bind
import xyz.nietongxue.common.processing.flatMap

data class AddNode<V : BaseGraph>(val node: Node) : Action<V, GraphLogData> {
    override fun action(): Reducer<V, GraphLogData> {
        return { graph ->
            graph.nodes_.add(node as BaseNode)
            resultAndLog(graph, Record(StringLogData("AddNode ${node.id}")))
        }
    }
}

data class Connect<V : BaseGraph>(val edge: Edge) : Action<V, GraphLogData> {
    override fun action(): Reducer<V, GraphLogData> {
        return { graph ->
            graph.edges_.add(edge)
            resultAndLog(graph, Record(StringLogData("Connect ${edge.from} to ${edge.to}")))
        }
    }
}

data class UpdateNode<V : BaseGraph>(
    val selector: NodeSelector<V>,
    val allowMultiMatch: Boolean = false,
    val nodeUpdater: (Node?) -> Node,
) :
    Action<V, GraphLogData> {
    override fun action(): Reducer<V, GraphLogData> {
        return { graph ->
            val nodes = selector.select(graph.nodes(), graph)
            when (allowMultiMatch) {
                false -> {
                    if (nodes.size > 1) {
                        justError(StopResult(StringLogData("UpdateNode failed: selector ${selector} matched ${nodes.size} nodes")))
                    } else {
                        val node = nodes.firstOrNull()
                        val newNode = nodeUpdater(node)
                        if (node != null)
                            graph.nodes_.remove(node)
                        graph.nodes_.add(newNode)
                        resultAndLog(graph, Record(StringLogData("UpdateNode ${node?.id}")))
                    }
                }

                else -> {
                    val b: ProcessingWithLog<V, GraphLogData> = bind(graph)
                    (nodes.fold(b) { acc, node ->
                        val newNode = nodeUpdater(node)
                        acc.flatMap {
                            it.nodes_.remove(node)
                            it.nodes_.add(newNode)
                            bind(it)
                        }
                    }).withLog(("UpdateNodes ${nodes.map { it.id }}").log())
                }
            }
        }
    }
}