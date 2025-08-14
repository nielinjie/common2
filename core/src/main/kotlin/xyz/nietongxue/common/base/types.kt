package xyz.nietongxue.common.base

interface Type<V> {
    val name: Path
    fun instanceOf(value: V): Boolean
}

interface Types<V> {
    val types: List<Type<V>>
    operator fun invoke(name: Path): Type<V>? {
        return types.find { it.name == name }
    }

    operator fun invoke(name: String): Type<V>? {
        return types.find { it.name == Path.fromString(name) }
    }
}
