package xyz.nietongxue.common.graph

import xyz.nietongxue.common.base.Name
import xyz.nietongxue.common.base.Quantifier
import xyz.nietongxue.common.graph.selectors.NodeSelector
import xyz.nietongxue.common.processing.*


/*
1. node types
2. edge types
3. port types
4. connection validation
5. connection situation
 */






open class NodeQuantifierRule<V : BaseGraph>(val nodes: NodeSelector<V>, val quantifier: Quantifier) :
    Rule<V, GraphLogData> {
    override fun check(value: V, phase: AssemblePhase): ProcessingWithLog<V, GraphLogData> {
        val number = nodes.select(value.nodes(), value).size
        return if (quantifier.match(number)) {
            bind(value)
        } else {
            justError(StopResult(StringLogData("NodeQuantifierRule failed")))
        }
    }

}


class NodePortRule<V : BaseGraph>(val sourceSelector: NodeSelector<V>, val portNames: List<Name>) : Rule<V, GraphLogData> {
    override fun check(value: V, phase: AssemblePhase): ProcessingWithLog<V, GraphLogData> {
        val sourceNodes = sourceSelector.select(value.nodes(), value)
        sourceNodes.forEach {
            val connections = value.edges(it.id)
            if (!connections.all { connection ->
                    portNames.contains(connection.on)
                }) {
                return justError(StopResult(StringLogData("NodePortRule failed")))
            }
        }
        return bind(value)
    }

}

class ConnectionTargetRule<V : BaseGraph>(
    val sourceSelector: NodeSelector<V>,
    val portName: Name,
    val targetSelector: NodeSelector<V>
) :
    Rule<V, GraphLogData> {
    override fun check(value: V, phase: AssemblePhase): ProcessingWithLog<V, GraphLogData> {
        val sourceNodes = sourceSelector.select(value.nodes(), value)
        sourceNodes.forEach {
            val connections = value.edges(it.id, portName)
            connections.forEach { connection ->
                val targetNodes = targetSelector.select(value.nodes(), value)
                if (!targetNodes.any { it.id == connection.to }) {
                    return@check justError(StopResult(StringLogData("ConnectionTargetRule failed")))
                }
            }
        }
        return bind(value)
    }
}

open class ConnectionQualityRule<V : BaseGraph>(
    val sourceSelector: NodeSelector<V>,
    val portName: Name,
    val quantifier: Quantifier
) :
    Rule<V, GraphLogData> {
    override fun check(value: V, phase: AssemblePhase): ProcessingWithLog<V, GraphLogData> {
        val sourceNodes = sourceSelector.select(value.nodes(), value)
        sourceNodes.forEach {
            val connections = value.edges(it.id, portName)
            if (!quantifier.match(connections.size)) {
                return@check justError(StopResult(StringLogData("ConnectionQualityRule failed")))
            }
        }
        return bind(value)
    }
}
