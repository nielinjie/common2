package xyz.nietongxue.common.tree


interface Tree<T> {
    val root: T
    fun children(node: T): List<T>
    fun nodeAndChildren(): List<Pair<T, List<T>>> {
        return this.nodeC()
    }

    fun nodeAndChild(): List<Pair<T, T>> {
        return this.nodeC().flatMap { p: Pair<T, List<T>> -> p.second.map { Pair(p.first, it) } }
    }

    fun bfs(): List<T> {
        return this.nodeAndChild().map { listOf(it.first, it.second) }.flatten().distinct()
    }

    private fun nodeC(parent: T = root): List<Pair<T, List<T>>> {
        val children = children(parent)
        val list: MutableList<Pair<T, List<T>>> = ArrayList()
        if (children.isNotEmpty()) {
            list.add(parent to children)
            list.addAll(children.map { nodeC(it) }.flatten())
        }
        return list
    }

    fun parent(node: T): T? {
        if (node == root) {
            return null
        }
        return this.nodeAndChild().firstOrNull { it.second == node }?.first
    }

    fun ancestor(node: T): List<T> {
        if (node == root) {
            return emptyList()
        }
        val parent: T = this.parent(node)!!
        return listOf(parent) + (this.ancestor(parent))
    }

    fun level(node: T): Int {
        return this.ancestor(node).size
    }
}

interface AddableTree<T> : Tree<T> {
    fun add(node: T, child: T) {
        this.add(node, listOf(child))
    }

    fun add(node: T, children: List<T>)
    fun add(node: T, subTree: Tree<T>) {
        this.add(node, subTree.root)
        subTree.nodeAndChildren().forEach {
            this.add(it.first, it.second)
        }
    }
}

interface MutableTree<T> : AddableTree<T> {
    fun remove(node: T)
    fun move(node: T, newParent: T) {
        this.remove(node)
        this.add(newParent, node)
    }
}

/**
 * 一个简单的树，用 map 存储结构。
 */
open class SimpleTree<T>(override val root: T) : AddableTree<T> {
    private var parentToChildren = mutableMapOf<T, MutableList<T>>()
    override fun add(node: T, children: List<T>) {
        val cr = parentToChildren.getOrPut(node) { mutableListOf() }
        cr.addAll(children)
    }

    override fun children(node: T): List<T> {
        return this.parentToChildren[node] ?: emptyList()
    }

    companion object {
        fun <T> fromNodePairs(pairs: List<Pair<T, T>>): SimpleTree<T> {
            val root: T = findRoot(pairs)
            val re = SimpleTree(root)
            pairs.forEach {
                re.add(it.first, it.second)
            }
            return re
        }

        fun <T> findRoot(pairs: List<Pair<T, T>>): T {
            val nodes = pairs.map { listOf(it.first, it.second) }.flatten().distinct()
            val hasParentNodes: List<T> = pairs.map { it.second }
            return nodes.find { !hasParentNodes.contains(it) } ?: throw IllegalArgumentException("no root find")
        }
    }
}

fun <T, U> mapTree(a: Tree<T>, mapFun: (T) -> U): Tree<U> {
    val aRoot = a.root
    val bRoot = mapFun(aRoot)
    val nodeCache: MutableMap<T, U> = mutableMapOf(aRoot to bRoot)
    val re = SimpleTree(bRoot)
    a.nodeAndChild().forEach { (p, c) ->
        val bp = nodeCache[p] ?: throw IllegalStateException("not parent mapped value find")
        val bc = mapFun(c)
        nodeCache[c] = bc
        re.add(bp, bc)
    }
    return re
}