package xyz.nietongxue.common.nameScope

import xyz.nietongxue.common.base.Name
import xyz.nietongxue.common.base.md5
import xyz.nietongxue.common.base.v3
import xyz.nietongxue.common.base.v7
import xyz.nietongxue.common.string.subStringList
import xyz.nietongxue.common.string.subStringStream


interface NameScopeStrategy {
    data object Hash : NameScopeStrategy // 直接使用 hash
    data object ShortestDistinct : NameScopeStrategy // 尝试截取一段最短的
    data object HashShortestDistinct : NameScopeStrategy // 先hash，再尝试截取最短
}

typealias Naming = Pair<Name, Name>

data class InnerNameScope(val names: List<Name>, val strategy: NameScopeStrategy) {
    init {
        require(names.distinct().size == names.size) {
            "duplicated names"
        }
    }

    /**
     * original names -> new names
     */
    val namings = when (strategy) {
        is NameScopeStrategy.Hash -> names.map {
            it to it.v3()
        }

        is NameScopeStrategy.ShortestDistinct -> {
            val sortedNames = names.sortedBy { it.length }
            shorted(sortedNames.map { it to it })
        }

        is NameScopeStrategy.HashShortestDistinct -> {
            shorted(names.map { it.md5() to it })
        }

        else -> TODO()
    }


    private fun shorted(proceedNames: List<Pair<String, String>>): List<Naming> {
        val re = mutableListOf<Naming>()
        proceedNames.forEach {
            val newNames = it.first.subStringStream() //找到备选的所有。TODO，可以扩展，比如在后面添加一些字符，还可以替换一些字符
            newNames.firstOrNull { tryingNew ->
                re.none { it.second == tryingNew }
            }?.also { findNew ->
                re.add(it.second to findNew)
            } ?: error("no distinct name find for - $it")
        }
        return re.toList()
    }


}

fun naming(originalName: String, nameScope: InnerNameScope): Pair<String, InnerNameScope> {
    val new = InnerNameScope(nameScope.names + originalName, nameScope.strategy)
    return new.namings.first { it.first == originalName }.second to new
}

class NameScope(val strategy: NameScopeStrategy) {
    var nameScope = InnerNameScope(emptyList(), strategy)
    fun add(originalName: String) {
        nameScope = InnerNameScope(nameScope.names + originalName, nameScope.strategy)
    }

    fun naming(originalName: String): String {
        val existed = nameScope.namings.firstOrNull { it.first == originalName }
        if (existed != null) return existed.second
        this.add(originalName)
        return nameScope.namings.first { it.first == originalName }.second
    }

    fun getARandomName(): String {
        val name = v7()
        return this.naming(name)
    }


}