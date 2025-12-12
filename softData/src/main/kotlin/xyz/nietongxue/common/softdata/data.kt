package xyz.nietongxue.common.softdata

import arrow.core.getOrElse
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.JsonNode
import xyz.nietongxue.common.properties.Property
import xyz.nietongxue.common.schema.ValidateResult
import xyz.nietongxue.common.schema.validate
import xyz.nietongxue.common.schema.validateWithData

data class InStructureProperty(val property: Property, val path: TransformPath) {
    fun TransformPath.lastName(): TransformPathNode.NamePathNode? {
        return path.lastOrNull {
            it is TransformPathNode.NamePathNode
        } as? TransformPathNode.NamePathNode
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