package xyz.nietongxue.common.collections

import xyz.nietongxue.common.base.Id
import xyz.nietongxue.common.base.IdGetter


interface Change<T> {
    data class Removed<V>(val id: Id) : Change<V>
    data class Added<V>(val value: V) : Change<V>
    data class Changed<V>(val value: V) : Change<V>
}

interface Diff {
    class Removed(val ids: List<Id>) : Diff
    class Added(val ids: List<Id>) : Diff
    class Changed(val ids: List<Id>) : Diff
}

data class Diffs(val removed: Diff.Removed, val added: Diff.Added, val changed: Diff.Changed)

fun <T> diff(fromOld: List<T>, toNew: List<T>, idGetter: IdGetter<T>, isChanged: (T, T) -> Boolean): Diffs {
    val fromIds = fromOld.mapNotNull { idGetter(it) }
    val toIds = toNew.mapNotNull { idGetter(it) }
    val removed = fromIds - toIds.toSet()
    val added = toIds - fromIds.toSet()
    val changed = fromIds.intersect(toIds.toSet()).filter { id ->
        val fromE = fromOld.find { idGetter(it) == id }!!
        val toE = toNew.find { idGetter(it) == id }!!
        isChanged(fromE, toE)
    }
    return Diffs(
        Diff.Removed(removed),
        Diff.Added(added),
        Diff.Changed(changed)
    )
}

fun <V> changes(
    fromOld: List<V>,
    toNew: List<V>,
    idGetter: IdGetter<V>,
    isChanged: (V, V) -> Boolean
): List<Change<V>> {
    val fromIds = fromOld.mapNotNull { idGetter(it) }
    val toIds = toNew.mapNotNull { idGetter(it) }
    val changed = fromIds.intersect(toIds.toSet()).filter { id ->
        val fromE = fromOld.find { idGetter(it) == id }!!
        val toE = toNew.find { idGetter(it) == id }!!
        isChanged(fromE, toE)
    }.map { id -> Change.Changed<V>(toNew.find { idGetter(it) == id }!!) }
    return fromIds.map { Change.Removed<V>(it) } + toIds.map { id -> Change.Added<V>(toNew.find { idGetter(it) == id }!!) } + changed
}

fun <V> patch(
    fromOld: List<V>, changes: List<Change<V>>, idGetter: IdGetter<V>,
): List<V> {
    val re: MutableList<V> = mutableListOf<V>().also { it.addAll(fromOld) }
    for (change in changes) {
        when (change) {
            is Change.Removed -> re.removeIf { idGetter(it) == change.id }
            is Change.Added -> re.add(change.value)
            is Change.Changed -> re.find { idGetter(it) == idGetter(change.value) }
                ?.let { re.set(re.indexOf(it), change.value) }
        }
    }
    return re.toList()
}
