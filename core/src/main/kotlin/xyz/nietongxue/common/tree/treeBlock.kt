package xyz.nietongxue.common.tree

interface Block {
    val children: MutableList<Block>
    var parent: Block?
    val index: Int
        get() = parent?.children?.indexOf(this) ?: -1

    fun olderSib(): Block? {
        return parent?.children?.getOrNull(index - 1)
    }

    fun youngerSib(): Block? {
        return parent?.children?.getOrNull(index + 1)
    }

    /*
    * 返回所有在后面的兄弟
     */
    fun youngerSiblings(count: Int? = null): List<Block> {
        return (parent?.children?.subList(index + 1, parent!!.children.size) ?: emptyList()).let {
            if (count != null) it.subList(0, count) else it
        }
    }

    fun olderSiblings(count: Int? = null): List<Block> {
        return (parent?.children?.subList(0, index) ?: emptyList()).let {
            if (count != null) it.subList(it.size - count, it.size) else it
        }
    }

    fun moveTo(newParent: Block) {
        parent?.children?.remove(this)
        newParent.children.add(this)
        parent = newParent
    }

    fun root(): Block {
        return parent?.root() ?: this
    }

    //深度优先遍历
    fun dfs(): List<Block> {
        return listOf(this) + children.flatMap { it.dfs() }
    }

    //广度优先遍历
    //TODO AI写的不知道对不对。
    fun bfs(): List<Block> {
        return sequenceOf(this).flatMap { it.children.asSequence() }.toList()
    }
}