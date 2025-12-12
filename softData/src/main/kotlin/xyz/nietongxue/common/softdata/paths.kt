package xyz.nietongxue.common.softdata

import xyz.nietongxue.common.json.JPath
import xyz.nietongxue.common.json.JPathNode
import xyz.nietongxue.common.properties.ArrayProperty
import xyz.nietongxue.common.properties.ObjectProperty
import xyz.nietongxue.common.properties.Property

fun Property.paths(
    paths: List<InStructureProperty> = emptyList(),
    currentPath: TransformPath = TransformPath(emptyList())
): List<InStructureProperty> {
    return when (this) {
        is ObjectProperty -> {
            paths + InStructureProperty(this, currentPath) + this.properties.map {
                it.value.paths(paths, currentPath.append(TransformPathNode.NamePathNode(it.key)))
            }.flatten()
        }

        is ArrayProperty -> {
            paths + InStructureProperty(this, currentPath) + this.item.paths(
                paths,
                currentPath.append(TransformPathNode.ItemPathNode)
            )
        }

        else -> paths + InStructureProperty(this, currentPath)
    }
}


data class TransformPath(val path: List<TransformPathNode>) {
    fun isConcrete(): Boolean {
        return path.all {
            when (it) {
                is TransformPathNode.NamePathNode -> true
                is TransformPathNode.IndexPathNode -> true
                is TransformPathNode.ItemPathNode -> false
            }
        }
    }

    fun append(node: TransformPathNode): TransformPath {
        return TransformPath(path + node)
    }

    fun up(): Pair<TransformPath, TransformPathNode>? {
        return if (path.isEmpty()) {
            null
        } else {
            TransformPath(path.dropLast(1)) to path.last()
        }
    }

    companion object {
        fun parse(whole: String): TransformPath {
            val parts = whole.split(".")
            require(parts.size > 1 && parts[0] == "$") {
                "transform path must start with $"
            }
            val path = parts.drop(1).map {
                when {
                    it == "[]" -> TransformPathNode.ItemPathNode

                    it.startsWith("[") -> {
                        val index = it.substring(1, it.length - 1).toInt()
                        TransformPathNode.IndexPathNode(index)
                    }

                    else -> {
                        TransformPathNode.NamePathNode(it)
                    }
                }
            }
            return TransformPath(path)
        }

        fun parse(jPath: JPath): TransformPath {
            return TransformPath(jPath.parts.map {
                when (it) {
                    is JPathNode.NameNode -> TransformPathNode.NamePathNode(it.name)
                    is JPathNode.IndexNode -> TransformPathNode.IndexPathNode(it.index)
                }
            })
        }

    }
}

sealed interface TransformPathNode {
    data class NamePathNode(val name: String) : TransformPathNode
    data class IndexPathNode(val index: Int) : TransformPathNode
    data object ItemPathNode : TransformPathNode
}