package xyz.nietongxue.common.nameScope

import xyz.nietongxue.common.base.Name
import xyz.nietongxue.common.base.md5
import xyz.nietongxue.common.base.v3
import xyz.nietongxue.common.string.subStringList


interface NameScopeStrategy {
    data object Hash : NameScopeStrategy
    data object ShortestDistinct : NameScopeStrategy
    data object HashShortestDistinct : NameScopeStrategy
}

typealias Naming = Pair<Name, Name>

data class NameScope(val names: List<Name>, val strategy: NameScopeStrategy) {
    init {
        require(names.distinct().size == names.size) {
            "duplicated names"
        }
    }

    val namings = when (strategy) {
        is NameScopeStrategy.Hash -> names.map {
            it to it.v3()
        }

        is NameScopeStrategy.ShortestDistinct -> {
            val sortedNames = names.sortedBy { it.length }
            shorted(sortedNames.map { it to it })
        }

        is NameScopeStrategy.HashShortestDistinct -> {
            shorted( names.map { it.md5() to it })
        }

        else -> TODO()
    }


    private fun shorted(proceedNames: List<Pair<String,String>>): List<Naming> {
        val re = mutableListOf<Naming>()
        proceedNames.forEach {
            val newNames = it.first.subStringList()
            newNames.firstOrNull { tryingNew ->
                re.none { it.second == tryingNew }
            }?.also { findNew ->
                re.add(it.second to findNew)
            } ?: error("no distinct name find for - $it")
        }
        return re.toList()
    }


}