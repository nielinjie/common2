package xyz.nietongxue.common.depends


data class DependencyGraph(val nodes: List<NodeWithDepends>) {
    fun topologicalSort(): List<NodeWithDepends> {
        val sorted = topologicalSortDFS(nodes)
        return sorted.map { nameString ->
            nodes.find { it.name == nameString }!!
        }
    }


    /**
     * 找到所有我依赖的。
     */
    fun listDependents(me: NodeWithDepends): List<NodeWithDepends> {
        return listDependents(me, nodes)
    }

    fun listDependents(myName: String): List<String> {
        return this.nodes.find { it.name == myName }?.let {
            listDependents(it, nodes).map { it.name }
        } ?: error("no node find - $myName")
    }

    /**
     * 找到所有依赖我的。
     */
    fun whoDependOnMe(me: NodeWithDepends): List<NodeWithDepends> {
        return whoDependOnMe(me, nodes)
    }

    fun whoDependOnMe(myName: String): List<String> {
        return this.nodes.find { it.name == myName }?.let {
            whoDependOnMe(it, nodes).map { it.name }
        } ?: error("no node find - $myName")
    }
}

data class NodeWithDepends(val name: String, val depends: List<String>)

fun topologicalSortDFS(objects: List<NodeWithDepends>): List<String> {
    val graph = mutableMapOf<String, List<String>>()
    for (obj in objects) {
        graph[obj.name] = obj.depends
    }

    val visited = mutableSetOf<String>()
    val stack = mutableListOf<String>()

    fun dfs(node: String) {
        if (node in visited) {
            return
        }
        visited.add(node)
        for (neighbor in graph[node]!!) {
            dfs(neighbor)
        }
        stack.add(node)
    }

    for (obj in objects) {
        dfs(obj.name)
    }

    return stack.reversed()
}

fun listDependents(current: NodeWithDepends, all: List<NodeWithDepends>): List<NodeWithDepends> {
    // 创建一个 Set 用于存储结果并避免循环依赖
    val result = mutableSetOf<NodeWithDepends>()

    // 创建一个映射，便于通过名称查找节点
    val nodeMap = all.associateBy { it.name }

    // 递归函数，用于查找所有依赖
    fun findDepends(node: NodeWithDepends) {
        // 如果已经处理过这个节点则返回
        if (result.contains(node)) return

        // 将当前节点添加到结果中
        result.add(node)

        // 遍历当前节点的直接依赖
        for (dependName in node.depends) {
            // 如果找到依赖节点，则递归处理
            nodeMap[dependName]?.let { dependNode ->
                findDepends(dependNode)
            } ?: error("depends not in all - $dependName")
        }
    }

    // 从给定的当前节点开始查找依赖
    findDepends(current)

    // 返回结果（不包含原始节点）
    return result.filter { it != current }
}

fun whoDependOnMe(me: NodeWithDepends, all: List<NodeWithDepends>): List<NodeWithDepends> {
    // 创建一个 Set 用于存储结果并避免循环依赖
    val result = mutableSetOf<NodeWithDepends>()

    // 创建一个映射，便于通过名称查找节点
    val nodeMap = all.associateBy { it.name }

    // 递归函数，用于查找所有依赖者
    fun findDependers(node: NodeWithDepends) {
        // 如果已经处理过这个节点则返回
        if (result.contains(node)) return

        // 将当前节点添加到结果中
        result.add(node)

        // 遍历所有节点，查找哪些节点直接依赖当前节点
        for (otherNode in all) {
            if (otherNode.depends.contains(node.name)) {
                findDependers(otherNode)
            }
        }
    }

    // 开始查找所有依赖于 me 的节点
    findDependers(me)

    // 返回结果（不包含原始节点）
    return result.filter { it != me }
}
