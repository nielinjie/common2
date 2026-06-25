package xyz.nietongxue.common.query

import xyz.nietongxue.common.base.Stuff


val stuffFields = object : Fields {
    override fun get(obj: Any, fieldName: String): Any? {
        return (obj as? Stuff ?: error("not a stuff"))[fieldName]
    }
}


fun matchStuff(stuff: Stuff, filter: Filter): Boolean {
    return Filtering(filter, stuffFields).match(stuff)
}

