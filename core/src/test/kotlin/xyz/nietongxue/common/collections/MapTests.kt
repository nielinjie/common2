package xyz.nietongxue.common.collections

import kotlin.test.Test

class MapTests {

    @Test
    fun test() {
        val map = mutableMapOf<String, Any>()
        map.nestedPut("a.b.c", 1)
    }

    @Test
    fun test2() {
        val map = mutableMapOf<String, Any>()
        map.nestedPut("a.b.c", 1)
        map.nestedPut("a.b.d", 2)
        map.nestedPut("a.e", 3)
        println(map)
    }

    @Test
    fun test3() {
        val map = mutableMapOf<String, Any>()
        map.nestedPut("a.b.c", 1)
        map.nestedPut("a.b.d", 2)
        map.nestedPut("a.e", 3)
        val re = map.nestedGet("a.b")
        println(re)
    }
}