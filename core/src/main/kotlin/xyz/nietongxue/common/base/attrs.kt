package xyz.nietongxue.common.base

typealias Attrs<T> = Map<String, T>


typealias PropertyList = Attrs<Any>
typealias Stuff = PropertyList

fun stuff() = mapOf<String, Any>()
fun stuff(vararg pairs: Pair<String, Any>) = pairs.toMap()