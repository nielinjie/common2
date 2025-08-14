package xyz.nietongxue.common.tree

import xyz.nietongxue.common.base.Id
import xyz.nietongxue.common.base.IdGetter


class ExternalTree<T>(root: T, private val idGetter: IdGetter<T>) : AddableTree<T> {
    private var nodes: MutableMap<Id, T> = mutableMapOf(idGetter(root)!! to root)
    private var tree = SimpleTree(idGetter(root))
    override fun add(node: T, children: List<T>) {
        nodes[idGetter(node)!!] = node
        nodes.putAll(children.map { idGetter(it)!! to it })
        tree.add(idGetter(node), children.map { idGetter(it) })
    }

    override val root: T
        get() = nodes[tree.root]!!

    override fun children(node: T): List<T> {
        val childrenId = tree.children(idGetter(node))
        return (childrenId.map { nodes.get(it) }).filterNotNull()
    }

    companion object {
        fun <T : Any> fromCollections(
            nodes: NodesCollection<T>,
            tree: TreeStructureCollection,
            idGetter: IdGetter<T>
        ): ExternalTree<T> {
            val nList: MutableMap<Id, T> = mutableMapOf(*(nodes.map { idGetter(it)!! to it }).toTypedArray())
            val rootId = SimpleTree.findRoot(tree)
            val re = ExternalTree(nodes.find { idGetter(it) == rootId }
                ?: throw IllegalArgumentException("not root find"),
                idGetter)
            re.nodes = nList
            re.tree = SimpleTree.fromNodePairs(tree)
            return re
        }
    }
}

typealias NodesCollection<T> = List<T>
typealias TreeStructureCollection = List<Pair<Id, Id>>



