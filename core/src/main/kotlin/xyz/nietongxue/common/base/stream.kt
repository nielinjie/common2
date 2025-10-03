package xyz.nietongxue.common.base



/*
类似于 version chain 的一条线。所以比较重要的是 end。从 end 可以推出一条线
TODO 叫 stream不太好。
 */

interface SingleStream<A : Any> {
    fun base(a: A): A?
    fun end(): A
}

fun <A : Any> SingleStream<A>.toList(): List<A> {
    val result = mutableListOf<A>()
    var current = end()
    while (true) {
        result.add(current)
        val next = base(current)
        if (next == null) {
            break
        } else {
            current = next
        }
    }
    return result.reversed()
}

interface Ordered<A : Any> {
    fun next(a: A): A?
    fun previous(a: A): A?
    fun head(): A?
    fun tail(): A?
    fun get(a: A): A?
}

fun <A : Any> List<A>.toOrdered(): Ordered<A> {
    return object : Ordered<A> {
        override fun next(a: A): A? {
            val index = indexOf(a)
            return if (index == size - 1) null else get(index + 1)
        }

        override fun previous(a: A): A? {
            val index = indexOf(a)
            return if (index == 0) null else get(index - 1)
        }

        override fun head(): A? {
            return firstOrNull()
        }

        override fun tail(): A? {
            return lastOrNull()
        }

        override fun get(a: A): A? {
            return if (a in this@toOrdered) a else null
        }

    }
}