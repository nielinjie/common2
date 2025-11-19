package xyz.nietongxue.common.bpmn

import com.alibaba.compileflow.engine.ProcessEngine
import com.alibaba.compileflow.engine.ProcessResult
import com.alibaba.compileflow.engine.ProcessSource
import com.alibaba.compileflow.engine.bpmn.definition.BpmnModel
import org.junit.jupiter.api.Assertions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.Test

@SpringBootTest
class TestMe() {

    @Autowired
    lateinit var engine: ProcessEngine<BpmnModel>

    val process = Process(
        "test", "test", listOf(
            StartEvent("start"),
            SequenceFlow("flow1", "start", "task1"),
            Task(
                "task1", ObjectMethodAction(
                    "xyz.nietongxue.common.bpmn.MyAdd", "add",
                    inputs = listOf(
                        Action.Input("a", "java.lang.Integer", "a"),
                        Action.Input("b", "java.lang.Integer", "b")
                    ), outputs = listOf(
                        Action.Output("sum", "java.lang.Integer", "sum")
                    )
                )
            ),
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
    val processSpring = Process(
        "test", "test", listOf(
            StartEvent("start"),
            SequenceFlow("flow1", "start", "task1"),
            Task(
                "task1", SpringBeanAction(
                    "MyAddBean","xyz.nietongxue.common.bpmn.MyAdd", "add",
                    inputs = listOf(
                        Action.Input("a", "java.lang.Integer", "a"),
                        Action.Input("b", "java.lang.Integer", "b")
                    ), outputs = listOf(
                        Action.Output("sum", "java.lang.Integer", "sum")
                    )
                )
            ),
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
    val processScript = Process(
        "test", "test", listOf(
            StartEvent("start"),
            SequenceFlow("flow1", "start", "task1"),
            Task(
                "task1", ScriptAction(
                    "groovy",
                    "a +b",
                    inputs = listOf(
                        Action.Input("a", "java.lang.Integer", "a"),
                        Action.Input("b", "java.lang.Integer", "b")
                    ), outputs = listOf(
                        Action.Output("sum", "java.lang.Integer", "sum")
                    )
                )
            ),
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

    @Test
    fun test() {
        val code = "simple_service"
        val result: ProcessResult<MutableMap<String?, Any?>?> =
            engine.execute(ProcessSource.fromCode(code), mapOf("a" to 1, "b" to 2))
        Assertions.assertTrue(result.isSuccess())
        Assertions.assertEquals(3, (result.data as Map<*, *>).get("serviceTask1Result"))
    }

    @Test
    fun testExecuteFromXml() {
        val code = "test"
        val content = process.toXML()
        val result: ProcessResult<MutableMap<String?, Any?>?> =
            engine.execute(ProcessSource.fromContent(code, content), mapOf("a" to 1, "b" to 2))
        Assertions.assertTrue(result.isSuccess())
        Assertions.assertEquals(3, (result.data as Map<*, *>).get("sum"))
    }


    @Test
    fun testExecuteFromXml2() {
        val code = "test"
        val content = processSpring.toXML()
        val result: ProcessResult<MutableMap<String?, Any?>?> =
            engine.execute(ProcessSource.fromContent(code, content), mapOf("a" to 1, "b" to 2))
        Assertions.assertTrue(result.isSuccess())
        Assertions.assertEquals(3, (result.data as Map<*, *>).get("sum"))
    }
    @Test
    fun testExecuteFromXml3() {
        val code = "test"
        val content = processScript.toXML()
        val result: ProcessResult<MutableMap<String?, Any?>?> =
            engine.execute(ProcessSource.fromContent(code, content), mapOf("a" to 1, "b" to 2))
        Assertions.assertTrue(result.isSuccess())
        Assertions.assertEquals(3, (result.data as Map<*, *>).get("sum"))
    }
}
