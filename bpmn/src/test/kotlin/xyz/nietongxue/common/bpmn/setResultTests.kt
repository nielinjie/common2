package xyz.nietongxue.common.bpmn

import com.alibaba.compileflow.engine.ProcessEngine
import com.alibaba.compileflow.engine.ProcessResult
import com.alibaba.compileflow.engine.ProcessSource
import com.alibaba.compileflow.engine.bpmn.definition.BpmnModel
import com.alibaba.compileflow.engine.bpmn.definition.Script
import org.junit.jupiter.api.Assertions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import xyz.nietongxue.common.bpmn.SequenceFlow
import kotlin.test.Test

@SpringBootTest
class SetResultTests {

    @Autowired
    lateinit var engine: ProcessEngine<BpmnModel>
    val inner = InnerProcess(
        name = "inner",
        elements = listOf(
            StartEvent("startInner"),
            SequenceFlow("flow2Inner", "startInner", "task1Inner"),
            Task(
                "task1Inner", ObjectMethodAction(
                    "xyz.nietongxue.common.bpmn.MyAppender", "append",
                    inputs = listOf(
                        Action.Input("i", "java.lang.Integer", "i"),
                        Action.Input("a", "java.lang.String", "p"),
                        Action.Input("b", "java.lang.String", "me"),
                    ), outputs = listOf(
                        Action.Output("result", "java.lang.String", "inner_result")
                    )
                )
            ),
            SequenceFlow(
                "flow3Inner", "task1Inner", "setResult"
            ),
            Task(name ="setResult", ScriptAction(
                "groovy", "resultMap.put(i,inner_result);",
                inputs = listOf(
                    Action.Input("i", "java.lang.Integer", "i"),
                    Action.Input("inner_result", "java.lang.String", "inner_result"),
                    Action.Input("resultMap", "java.util.HashMap<String,String>", "resultMap")
                ),
            )),
            SequenceFlow(
                "flow4Inner", "setResult", "endInner"
            ),
            EndEvent("endInner"),
        )
    )
    val process = Process(
        "test", "test", listOf(
            StartEvent("start"),
            SequenceFlow("flow1", "start", "newResultMap"),
            Task("newResultMap", ScriptAction(
                "groovy",
                "new HashMap<String,String>()",
                outputs = listOf(
                    Action.Output("resultMap", "java.util.HashMap<String,String>", "resultMap")
                ),
            )),
            SequenceFlow("flow15", "newResultMap", "loop"),
            Loop("loop", "collection", "p", "i", "java.lang.String", inner),
            SequenceFlow("flow2", "loop", "end"),
            EndEvent("end"),
        ),
        inputs = listOf(
            Input("collection", "java.util.ArrayList<String>"),
            Input("me", "java.lang.String"),
            Input("inner_result", "java.lang.String")
        ),
        outputs = listOf(
            Output("resultMap", "java.util.HashMap<String,String>")
        )

    )


    @Test
    fun test() {
        val code = "test"
        val content = process.toXML().also {
            println(it)
        }
        val map = HashMap<String, String>()
        val result: ProcessResult<MutableMap<String?, Any?>?> =
            engine.execute(ProcessSource.fromContent(code, content), mapOf("collection" to ArrayList<String>().also {
                it.add("alice")
                it.add("bob")
            }, "me" to "zark",))
        Assertions.assertTrue(result.isSuccess())
        println(result.data)
    }
}