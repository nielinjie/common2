package xyz.nietongxue.common.base

interface Selector<I, in C> {
    fun select(input: List<I>, context: C): List<I>
}

fun <I, C> Selector<I, C>.pipe(selector: Selector<I, C>): Selector<I, C> {
    return object : Selector<I, C> {
        override fun select(input: List<I>, context: C): List<I> {
            return selector.select(this@pipe.select(input, context), context)
        }
    }
}

fun <I, C> Selector<I, C>.or(selector: Selector<I, C>): Selector<I, C> {
    return object : Selector<I, C> {
        override fun select(input: List<I>, context: C): List<I> {
            return this@or.select(input, context) + selector.select(input, context)
        }
    }
}


fun <I, C> Selector<I, C>.and(fn: (I) -> Boolean): Selector<I, C> {
    return object : Selector<I, C> {
        override fun select(input: List<I>, context: C): List<I> {
            return this@and.select(input, context).filter(fn)
        }
    }
}


//fun <I, C> Type<I>.selector(): Selector<I, C> {
//    return object : Selector<I, C> {
//        override fun select(input: List<I>, context: C): List<I> {
//            return input.filter {
//                instanceOf(it)
//            }
//        }
//    }
//}

