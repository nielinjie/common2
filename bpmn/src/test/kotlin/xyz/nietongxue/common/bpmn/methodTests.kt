package xyz.nietongxue.common.bpmn

import kotlin.test.Test

class MethodFindTests {
    @Test
    fun test() {
        val clazz = Class.forName("xyz.nietongxue.common.bpmn.MyAppender2")
        val method = MethodFinder.findMethod(clazz, MethodSearchCriteria("append", listOf("java.lang.String", "java.lang.String")))
        println(method)
    }
    @Test
    fun test1() {
        val clazz = Class.forName("xyz.nietongxue.common.bpmn.MyAppender2")
        val method = MethodFinder.findMethod(clazz, MethodSearchCriteria("append", listOf("java.lang.String", "java.lang.Object")))
        println(method)
    }
    @Test
    fun test2() {
        val clazz = Class.forName("xyz.nietongxue.common.bpmn.MyAppender2")
        val method = MethodFinder.findMethod(clazz, MethodSearchCriteria("append", listOf("java.lang.String", "java.lang.Object","java.lang.Integer")))
        println(method)
    }
}
