package xyz.nietongxue.common.softdata

import xyz.nietongxue.common.properties.ArrayProperty
import xyz.nietongxue.common.properties.ObjectProperty
import xyz.nietongxue.common.properties.Property
import xyz.nietongxue.common.properties.SimpleProperty
import xyz.nietongxue.common.schema.DataSchema


// 一套可变的 property 结构主要用于构建。

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

fun BuildingProperty.get(path: TransformPath) = this.get(path.path)
fun BuildingProperty.get(nodes: List<TransformPathNode>): BuildingProperty? {
    if (nodes.isEmpty()) return this
    var index = 0
    var currentNode: TransformPathNode
    var currentProperty: BuildingProperty = this
    while (index < nodes.size) {
        currentNode = nodes[index]
        when (currentProperty) {
            is BuildingProperty.ArrayBuilding -> {
                when (currentNode) {
                    is TransformPathNode.ItemPathNode -> currentProperty = currentProperty.item!!
                    is TransformPathNode.IndexPathNode -> currentProperty = currentProperty.item!!
                    is TransformPathNode.NamePathNode -> error("not array - $currentProperty")
                }
            }

            is BuildingProperty.ObjectBuilding -> {
                when (currentNode) {
                    is TransformPathNode.ItemPathNode -> error("not object - $currentProperty")
                    is TransformPathNode.IndexPathNode -> error("not object - $currentProperty")
                    is TransformPathNode.NamePathNode -> currentProperty =
                        (currentProperty.properties.get(currentNode.name)
                            ?: error("property not found - ${currentNode.name}"))
                }
            }

            else -> error("not property")
        }

        index++
    }
    return currentProperty
}
