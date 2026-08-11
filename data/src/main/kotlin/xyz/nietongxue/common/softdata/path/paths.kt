package xyz.nietongxue.common.softdata.path

import xyz.nietongxue.common.json.JPath
import xyz.nietongxue.common.json.JPathNode
import xyz.nietongxue.common.properties.ArrayProperty
import xyz.nietongxue.common.properties.ObjectProperty
import xyz.nietongxue.common.properties.Property
import xyz.nietongxue.common.softdata.InStructureProperty

fun Property.paths(
    paths: List<InStructureProperty> = emptyList(),
    currentPath: Path = Path(emptyList())
): List<InStructureProperty> {
    return when (this) {
        is ObjectProperty -> {
            paths + InStructureProperty(this, currentPath) + this.properties.map {
                it.value.paths(paths, currentPath.append(PathNode.NameNode(it.key)))
            }.flatten()
        }

        is ArrayProperty -> {
            paths + InStructureProperty(this, currentPath) + this.item.paths(
                paths,
                currentPath.append(PathNode.AllItemNode)
            )
        }

        else -> paths + InStructureProperty(this, currentPath)
    }
}


data class Path(val path: List<PathNode>) {
    fun isConcrete(): Boolean {
        return path.all {
            it.isConcrete()
        }
    }

    fun append(node: PathNode): Path {
        return Path(path + node)
    }

    fun up(): Pair<Path, PathNode>? {
        return if (path.isEmpty()) {
            null
        } else {
            Path(path.dropLast(1)) to path.last()
        }
    }

    companion object {
        fun parse(whole: String): Path {
            val parts = whole.split(".")
            require(parts.size > 1 && parts[0] == "$") {
                "transform path must start with $"
            }
            val path = parts.drop(1).map {
                when {
                    it == "[]" -> PathNode.AllItemNode

                    it.startsWith("[") -> {
                        val index = it.substring(1, it.length - 1).toInt()
                        PathNode.IndexNode(index)
                    }

                    else -> {
                        PathNode.NameNode(it)
                    }
                }
            }
            return Path(path)
        }

        fun fromJPath(jPath: JPath): Path {
            return Path(jPath.parts.map {
                when (it) {
                    is JPathNode.NameNode -> PathNode.NameNode(it.name)
                    is JPathNode.IndexNode -> PathNode.IndexNode(it.index)
                }
            })
        }

    }
}

sealed interface PathNode {
    fun isConcrete(): Boolean
    data class NameNode(val name: String) : PathNode {
        override fun isConcrete(): Boolean {
            return true
        }
    }

    data class IndexNode(val index: Int) : PathNode {
        override fun isConcrete(): Boolean {
            return true
        }
    }

    /**
     * 对于 ArrayProperty，取所有的 item。有可能有两个用途：
     * - 1. 只关心 type 的时候，直接 itemNode，不需要 indexNode，
     * - 2. 取所有item 的值，一对多映射。这个用途少见。
     *
     * 而对应的，ObjectProperty 这两个用途似乎没意义。
     */
    data object AllItemNode : PathNode {
        override fun isConcrete(): Boolean {
            return false
        }
    }
}