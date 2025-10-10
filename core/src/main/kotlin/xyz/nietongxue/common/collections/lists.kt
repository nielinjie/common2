package xyz.nietongxue.common.collections

fun <T> List<T>.replaceAt(index: Int, element: T): List<T> {
    return this.replaceAt(index, listOf(element))
}

fun <T> List<T>.replaceAt(index: Int, element: List<T>): List<T> {
    val before = this.subList(0, index)
    val after = this.subList(index + 1, this.size)
    return before + element + after
}

fun <T> List<T>.replaceWhen(element: T, condition: (T) -> Boolean): List<T> {
    val index = this.indexOfFirst { condition(it) }
    return if (index == -1) this else this.replaceAt(index, element)
}


fun <T : Any, K : Any, V : Any> List<T>.associateNotNull(transform: (T) -> Pair<K?, V?>): Map<K, V> {
    return (this.map { transform(it) }.filter {
        it.first != null && it.second != null
    } as List<Pair<K, V>>).toMap()
}

fun <T> List<T>.replaceWhen2(element: (T) -> T, condition: (T) -> Boolean): List<T> {
    val index = this.indexOfFirst { condition(it) }
    return if (index == -1) this else this.replaceAt(index, element(this[index]))
}

fun <T> List<T>.replaceOrAdd(element: (T?) -> T, condition: (T) -> Boolean): List<T> {
    val index = this.indexOfFirst { condition(it) }
    return if (index == -1) this.plus(element(null)) else this.replaceAt(index, element(this[index]))
}
