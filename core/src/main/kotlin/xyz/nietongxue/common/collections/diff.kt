package xyz.nietongxue.common.collections

import xyz.nietongxue.common.base.Id
import xyz.nietongxue.common.base.IdGetter

interface Change {
    object Removed : Change
    object Added : Change
    object Changed : Change
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