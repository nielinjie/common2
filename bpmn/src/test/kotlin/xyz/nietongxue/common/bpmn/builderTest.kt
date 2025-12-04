package xyz.nietongxue.common.bpmn

import kotlin.test.Test

class BuilderTest {
    @Test
    fun test() {
        val elements = elements {
            start().connect(end())
        }
        println(elements)
    }

    @Test
    fun test2() {
        val elements = elements {
            start().connect(
                Task("task1", ScriptAction("groovy", "1+1"))
            ).connect(end())
        }
        println(elements)
    }
}