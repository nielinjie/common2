package xyz.nietongxue.common.properties

import xyz.nietongxue.common.softdata.BuildingProperty
import xyz.nietongxue.common.softdata.building
import xyz.nietongxue.common.softdata.get
import xyz.nietongxue.common.softdata.path.Path
import xyz.nietongxue.common.softdata.path.PathNode


fun Property.get(path: Path) = this.get(path.path)
fun Property.get(nodes: List<PathNode>): Property { //也可以用搜索的办法，property.paths.find(it.path == path(nodes))
    if (nodes.isEmpty()) return this
    var index = 0
    var currentNode: PathNode
    var currentProperty: Property = this
    while (index < nodes.size) {
        currentNode = nodes[index]
        when (currentProperty) {
            is ArrayProperty -> {
                when (currentNode) {
                    is PathNode.AllItemNode -> currentProperty = currentProperty.item
                    is PathNode.IndexNode -> currentProperty = currentProperty.item
                    is PathNode.NameNode -> error("not array - $currentProperty")
                }
            }

            is ObjectProperty -> {
                when (currentNode) {
                    is PathNode.AllItemNode -> error("not object - $currentProperty")
                    is PathNode.IndexNode -> error("not object - $currentProperty")
                    is PathNode.NameNode -> currentProperty =
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

fun Property.transform(path: Path, replaceFun: (source: BuildingProperty) -> BuildingProperty): Property {
    val up = path.up() ?: error("empty path not supported")
    val building = this.building()
    val upLevelNode = building.get(up.first)
    when (val node = up.second) {
        is PathNode.AllItemNode, is PathNode.IndexNode -> {
            when (upLevelNode) {
                is BuildingProperty.ArrayBuilding -> {
                    val item = upLevelNode.item!!
                    val newItem = replaceFun(item)
                    upLevelNode.item = newItem
                }

                else -> error("not array")
            }
        }

        is PathNode.NameNode -> {
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

