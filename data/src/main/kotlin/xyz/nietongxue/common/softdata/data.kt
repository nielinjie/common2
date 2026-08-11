package xyz.nietongxue.common.softdata

import arrow.core.getOrElse
import com.fasterxml.jackson.annotation.JsonIgnore
import xyz.nietongxue.common.properties.Property
import xyz.nietongxue.common.schema.ValidateResult
import xyz.nietongxue.common.schema.validateWithData
import xyz.nietongxue.common.softdata.path.Path
import xyz.nietongxue.common.softdata.path.PathNode
import xyz.nietongxue.common.softdata.path.paths

/**
 * 在 structure 中的 property，所以有 path，是相对于 structure root 的。
 */
data class InStructureProperty(val property: Property, val path: Path) {
    fun Path.lastName(): PathNode.NameNode? {
        return path.lastOrNull {
            it is PathNode.NameNode
        } as? PathNode.NameNode
    }

    @JsonIgnore
    val nearestName: String? = path.lastName()?.name
}


class DataStructure(
    val rootProperty: Property
) {
    @get:JsonIgnore
    val schema get() = rootProperty.schema

    private var va_: Any? = null

    @get:JsonIgnore
    var value: Any?
        get() = va_
        set(value) {
            va_ = validate(value).getOrElse { error("validate error - $it") }
        }


    fun paths(): List<InStructureProperty> {
        return rootProperty.paths()
    }

    fun validate(value: Any?): ValidateResult {
        return validateWithData(value, rootProperty.schema)
    }


}