package xyz.nietongxue.common.nameScope

import org.junit.jupiter.api.extension.ExtensionContext
import kotlin.test.Test

class NameTest {
    @Test
    fun test(){
        val nameScope = NameScope(
            listOf("a","b")
            , NameScopeStrategy.Hash
        )
        println(nameScope.namings)
    }
    @Test
    fun testShort(){
        val nameScope = NameScope(
            listOf("ab","abc","abdef","cd","a")
            , NameScopeStrategy.ShortestDistinct
        )
        println(nameScope.namings)
    }
    @Test
    fun testShort2(){
        val nameScope = NameScope(
            listOf("ab","abc","abdef","cd","a","1","3","2","f","5")
            , NameScopeStrategy.HashShortestDistinct
        )
        println(nameScope.namings)
    }
}