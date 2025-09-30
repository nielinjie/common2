package xyz.nietongxue.common.coordinate

import xyz.nietongxue.common.base.Name
import xyz.nietongxue.common.json.JsonWithType


interface Located {
    val location: Location
    val coordinate: Coordinate
}

data class Coordinate(
    val dimensions: List<Dimension>
) {
    fun dimension(name: Name): Dimension? {
        return dimensions.singleOrNull { it.name == name }
    }
}


fun Coordinate.validate(location: Location): Boolean {
    location.values.forEach {
        val dimension = dimension(it.dimensionName)
        if (dimension == null) {
            return false
        }else{
            TODO()
        }
    }
    return TODO()
}


/*
一个完备（是否过于复杂）的定位机制。
一个定位包括多个维度的坐标点。
每个维度可以有多种类型，比如数字、分类、tree 等。
 */
data class Location(
    val values: List<Value>
)

@JsonWithType
interface Dimension {
    val name: Name
}

data class Value(
    val dimensionName: Name,
    val value: Any
)


data class NumberDimension<T : Number>(override val name: String) : Dimension


data class CategoryDimension(override val name: String, val categories: List<String>) : Dimension

data class OrderedDimension(override val name: String, val ordered: List<String>) : Dimension

data class PathLikeDimension(override val name: String) : Dimension





