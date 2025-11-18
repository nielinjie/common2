package xyz.nietongxue.common.bpmn

import kotlin.test.Test

class XMLTest {
    @Test
    fun test() {
        val process = Process(
            "test", "test",listOf(
                StartEvent("start"),
                SequenceFlow("flow1", "start", "task1"),
                Task("task1", ObjectMethodAction("xyz.nietongxue.common.bpmn.MyAdd","add",
                    inputs = listOf(
                        Action.Input("a", "java.lang.Integer", "a"),
                        Action.Input("b", "java.lang.Integer", "b")
                    ), outputs = listOf(
                        Action.Output("sum", "java.lang.Integer", "sum")
                    ))),
                SequenceFlow("flow2", "task1", "end"),
                EndEvent("end"),
            ),
            inputs = listOf(
                Input("a", "java.lang.Integer"),
                Input("b", "java.lang.Integer")
            ),
            outputs = listOf(
                Output("sum", "java.lang.Integer")
            )
        )
        process.toXML().also {
            println(it)
        }
    }
}