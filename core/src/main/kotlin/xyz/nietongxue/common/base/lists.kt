package xyz.nietongxue.common.base


typealias CompositedList<A> = List<CompositedListItem<A>>

/*
把一个list里面相“像”的项合并到一起。
 */
interface CompositedListItem<A : Any>
data class Single<A : Any>(val index: Int, val value: A) : CompositedListItem<A>
data class Composited<A : Any>(val startIndex: Int) : CompositedListItem<A> {
    val values = mutableListOf<A>()
}

fun <A : Any> List<A>.toComposited(canComposite: (A) -> Boolean): CompositedList<A> {
    val nearest = findNearest(this, canComposite)
    return com(nearest, this)
}

fun <A : Any> findNearest(list: List<A>, f: (A) -> Boolean): List<Int> {
    val re = mutableListOf<Int>()
    for (i in list.indices) {
        if (f(list[i])) {
            if (i == 0 || re[i - 1] == -1) {
                re.add(i)
            } else {
                re.add(re[i - 1])
            }
        } else {
            re.add(-1)
        }
    }
    return re
}


fun <A : Any> com(list: List<Int>, valueList: List<A>): List<CompositedListItem<A>> {
    require(list.size == valueList.size)
    val re = mutableListOf<CompositedListItem<A>>()
    for (i in list.withIndex()) {
        if (i.value == -1) {
            re.add(Single(i.index, valueList[i.index]))
        } else {
            if (re.isEmpty() || re.last() is Single) {
                re.add(Composited(i.value))
            }
            (re.last() as Composited).values.add(valueList[i.index])
        }
    }
    return re
}

fun <T> List<T>.findAndModify(findFn:(T)->Boolean,modifyFn:(T)->T):List<T>{
    return this.map {
        if(findFn(it)){
            modifyFn(it)
        }else{
            it
        }
    }
}