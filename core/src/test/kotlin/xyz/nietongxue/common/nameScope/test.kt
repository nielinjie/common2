package xyz.nietongxue.common.nameScope

import kotlin.test.Test

class NameTest {
    @Test
    fun test() {
        val nameScope = InnerNameScope(
            listOf("a", "b"), NameScopeStrategy.Hash
        )
        println(nameScope.namings)
    }

    @Test
    fun testShort() {
        val nameScope = InnerNameScope(
            listOf("ab", "abc", "abdef", "cd", "a"), NameScopeStrategy.ShortestDistinct
        )
        println(nameScope.namings)
    }

    @Test
    fun testShort2() {
        val nameScope = InnerNameScope(
            listOf("ab", "abc", "abdef", "cd", "a", "1", "3", "2", "f", "5"), NameScopeStrategy.HashShortestDistinct
        )
        println(nameScope.namings)
    }

    @Test
    fun useCase() {


        val nameScope = InnerNameScope(emptyList(), NameScopeStrategy.HashShortestDistinct)
        val (newName, newNameScope) = naming("a", nameScope)
        val (newName2, newNameScope2) = naming("b", newNameScope)
        println(newName)
        println(newName2)
        println(newNameScope2.namings)
    }

    @Test
    fun useCase2() {
        val nameScope = NameScope(NameScopeStrategy.HashShortestDistinct)
        val newName = nameScope.naming("a")
        val newName2 = nameScope.naming("b")
        println(newName)
        println(newName2)
        println(nameScope.nameScope.namings)

        for (i in 0..10) {
            println(nameScope.getARandomName())

        }
        println(nameScope.nameScope.namings)

    }
    @Test
    fun useCase3() {
        val nameScope = NameScope(NameScopeStrategy.ShortestDistinct)

        for (i in 0..10) {
            println(nameScope.getARandomName())

        }
        println(nameScope.nameScope.namings)

    }
}