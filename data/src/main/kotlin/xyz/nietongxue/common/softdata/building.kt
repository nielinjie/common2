package xyz.nietongxue.common.softdata

import xyz.nietongxue.common.properties.ArrayProperty
import xyz.nietongxue.common.properties.ObjectProperty
import xyz.nietongxue.common.properties.Property
import xyz.nietongxue.common.properties.SimpleProperty
import xyz.nietongxue.common.schema.DataSchema
import xyz.nietongxue.common.softdata.path.Path
import xyz.nietongxue.common.softdata.path.PathNode


// 一套可变的 property 结构,主要用于构建。
// 主要是 object 类的可变？

interface BuildingProperty {
    class ObjectBuilding(val properties: MutableMap<String, BuildingProperty>) : BuildingProperty {
        override fun finish(): Property {
            return ObjectProperty(properties.mapValues { it.value.finish() })
        }
    }

    class ArrayBuilding(var item: BuildingProperty?, schema: DataSchema) : BuildingProperty {
        override fun finish(): Property {
            return ArrayProperty(item!!.finish())
        }
    }

    class SimpleBuilding(val property: SimpleProperty) : BuildingProperty {
        override fun finish(): Property {
            return property
        }
    }

    fun finish(): Property
}

fun Property.building(): BuildingProperty {
    return when (this) {
        is ObjectProperty -> BuildingProperty.ObjectBuilding(properties.mapValues { it.value.building() }
            .toMutableMap())

        is ArrayProperty -> BuildingProperty.ArrayBuilding(item.building(), item.schema)
        is SimpleProperty -> BuildingProperty.SimpleBuilding(this)
        else -> throw IllegalArgumentException("unknown property type")
    }
}

fun BuildingProperty.get(path: Path) = this.get(path.path)
fun BuildingProperty.get(nodes: List<PathNode>): BuildingProperty? {
    if (nodes.isEmpty()) return this
    var index = 0
    var currentNode: PathNode
    var currentProperty: BuildingProperty = this
    while (index < nodes.size) {
        currentNode = nodes[index]
        when (currentProperty) {
            is BuildingProperty.ArrayBuilding -> {
                currentProperty = when (currentNode) {
                    is PathNode.AllItemNode -> currentProperty.item!!
                    is PathNode.IndexNode -> currentProperty.item!!
                    is PathNode.NameNode -> error("not array - $currentProperty")
                }
            }

            is BuildingProperty.ObjectBuilding -> {
                currentProperty = when (currentNode) {
                    is PathNode.AllItemNode -> error("not object - $currentProperty")
                    is PathNode.IndexNode -> error("not object - $currentProperty")
                    is PathNode.NameNode -> (currentProperty.properties.get(currentNode.name)
                        ?: error("property not found - ${currentNode.name}"))
                }
            }

            else -> error("not property")
        }

        index++
    }
    return currentProperty
}
