package xyz.nietongxue.common.base

import com.fasterxml.jackson.annotation.JsonIgnore

typealias Name = String

interface HasName {
    val name: Name
}


data class FullName(val name: String, val namespace: String) {
    @field:JsonIgnore
    val string = "$namespace/$name"
}

interface HasNamespace {
    val namespace: String
    fun fullName(): FullName {
        require(this is HasName)
        return FullName(this.name, this.namespace)
    }
}


