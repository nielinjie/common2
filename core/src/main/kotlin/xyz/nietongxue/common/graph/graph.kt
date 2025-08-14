package xyz.nietongxue.common.graph

import xyz.nietongxue.common.base.HasId
import xyz.nietongxue.common.base.HasName
import xyz.nietongxue.common.base.Id
import xyz.nietongxue.common.base.Name




interface Graph {
    fun nodes(): List<Node>
    fun edges(): List<Edge>
}

interface Node : HasId {
}

interface Port : HasName


interface Edge {
    val from: Id
    val to: Id
    val on: Name
}


open class BaseGraph : Graph {
    val nodes_ = mutableListOf<Node>()
    val edges_ = mutableListOf<Edge>()


    override fun nodes(): List<Node> {
        return this.nodes_
    }

    override fun edges(): List<Edge> {
        return this.edges_
    }

    fun edges(nodeId: Id): List<Edge> {
        return nodes_.find { it.id == nodeId }?.let { node ->
            edges_.filter { it.from == node.id }
        } ?: emptyList()
    }

    fun edges(nodeId: Id, portName: Name): List<Edge> {
        return edges(nodeId).filter {
            it.on == portName
        }
    }
    fun node(id:Id):Node?{
        return nodes_.find { it.id==id }
    }
    fun edge(from:Id, on:Name, to:Id):Edge?{
        return edges_.find { it.from==from && it.on==on && it.to==to }
    }
}

open class BaseNode(override val id: Id) : Node {
}

open class BaseEdge(
    override val from: Id,
    override val on: Name,
    override val to: Id
) : Edge

class BasePort(override val name: Name) : Port {

}

