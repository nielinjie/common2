package xyz.nietongxue.common.bpmn

import xyz.nietongxue.common.base.MethodSearchCriteria
import xyz.nietongxue.common.base.findMethod
import kotlin.test.Test
import kotlin.test.assertNull

class MethodFindTests {
    @Test
    fun test() {
        val clazz = Class.forName("xyz.nietongxue.common.bpmn.MyAppender2")
        val method = findMethod(
            clazz,
            MethodSearchCriteria("append", listOf("java.lang.String", "java.lang.String").map { Class.forName(it) })
        )
        println(method)
    }

    @Test
    fun test1() {
        val clazz = Class.forName("xyz.nietongxue.common.bpmn.MyAppender2")
        val method =
            findMethod(
                clazz,
                MethodSearchCriteria("append", listOf("java.lang.String", "java.lang.Object").map {
                    Class.forName(it)
                })
            )
        //找不到，string，object 无法传入 append(string,string,integer)或者append(string,string) 对的。
        assertNull(method)
    }

    @Test
    fun test2() {
        val clazz = Class.forName("xyz.nietongxue.common.bpmn.MyAppender2")
        val method = findMethod(
            clazz,
            MethodSearchCriteria("append", listOf("java.lang.String", "java.lang.Object", "java.lang.Integer").map {
                Class.forName(it)
            })
        )
        //找不到，string，object，integer 无法传入 append(string,string,integer) 对的。
        assertNull(method)
    }

    @Test
    fun superTest() {
        val clA = Class.forName("java.lang.String")
        val clB = Class.forName("java.lang.Object")
        println(clB.isAssignableFrom(clA))


    }

    @Test
    fun superTest2() {
        val clA = Class.forName("java.lang.Integer")
        val clB = Class.forName("java.lang.Object")
        println(clB.isAssignableFrom(clA))
    }
}
