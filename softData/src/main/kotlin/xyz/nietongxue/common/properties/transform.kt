package xyz.nietongxue.common.properties

import xyz.nietongxue.common.softdata.BuildingProperty
import xyz.nietongxue.common.softdata.InStructureProperty
import xyz.nietongxue.common.softdata.TransformPath
import xyz.nietongxue.common.softdata.TransformPathNode
import xyz.nietongxue.common.softdata.building
import xyz.nietongxue.common.softdata.get


fun Property.get(path: TransformPath) = this.get(path.path)
fun Property.get(nodes: List<TransformPathNode>): Property { //也可以用搜索的办法，property.paths.find(it.path == path(nodes))
    if (nodes.isEmpty()) return this
    var index = 0
    var currentNode: TransformPathNode
    var currentProperty: Property = this
    while (index < nodes.size) {
        currentNode = nodes[index]
        when (currentProperty) {
            is ArrayProperty -> {
                when (currentNode) {
                    is TransformPathNode.ItemPathNode -> currentProperty = currentProperty.item
                    is TransformPathNode.IndexPathNode -> currentProperty = currentProperty.item
                    is TransformPathNode.NamePathNode -> error("not array - $currentProperty")
                }
            }

            is ObjectProperty -> {
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
fun Property.transform(path: TransformPath, replaceFun: (source: BuildingProperty) -> BuildingProperty): Property {
    val up = path.up() ?: error("empty path not supported")
    val building = this.building()
    val upLevelNode = building.get(up.first)
    when (val node = up.second) {
        is TransformPathNode.ItemPathNode, is TransformPathNode.IndexPathNode -> {
            when (upLevelNode) {
                is BuildingProperty.ArrayBuilding -> {
                    val item = upLevelNode.item!!
                    val newItem = replaceFun(item)
                    upLevelNode.item = newItem
                }
                else -> error("not array")
            }
        }

        is TransformPathNode.NamePathNode -> {
            when (upLevelNode) {
                is BuildingProperty.ObjectBuilding -> {
                    val property = upLevelNode.properties[node.name]!!
                    val newProperty = replaceFun(property)
                    upLevelNode.properties[node.name] = newProperty
                }
                else -> error("not object")
            }
        }

    }
    return building.finish()
}

