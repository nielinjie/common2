package xyz.nietongxue.common.bpmn

import com.alibaba.compileflow.engine.ProcessEngine
import com.alibaba.compileflow.engine.ProcessResult
import com.alibaba.compileflow.engine.ProcessSource
import com.alibaba.compileflow.engine.bpmn.definition.BpmnModel
import org.junit.jupiter.api.Assertions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import xyz.nietongxue.common.bpmn.SequenceFlow
import kotlin.test.Test

@SpringBootTest
class FLowTest {

    @Autowired
    lateinit var engine: ProcessEngine<BpmnModel>
    val inner = InnerProcess(
        name = "inner",
        elements = listOf(
            StartEvent("startInner"),
            SequenceFlow("flow1Inner", "startInner", "task1Inner"),
            Task(
                "task1Inner", ObjectMethodAction(
                    "xyz.nietongxue.common.bpmn.MyAppender", "append",
                    inputs = listOf(
                        Action.Input("a", "java.lang.String", "p"),
                        Action.Input("b", "java.lang.String", "me"),
                        Action.Input("i", "java.lang.Integer", "i"),
                        Action.Input("resultMap", "java.util.HashMap<String,String>", "resultMap")
                    ), outputs = listOf(
                    )
                )
            ),
            SequenceFlow(
                "flow2Inner", "task1Inner", "endInner"
            ),
            EndEvent("endInner"),
        )
    )
    val process = Process(
        "test", "test", listOf(
            StartEvent("start"),
            SequenceFlow("flow1", "start", "loop"),
            Loop("loop", "collection", "p", "i", "java.lang.String", inner),
            SequenceFlow("flow2", "loop", "end"),
            EndEvent("end"),
        ),
        inputs = listOf(
            Input("collection", "java.util.ArrayList<String>"),
            Input("me", "java.lang.String"),
            Input("resultMap", "java.util.HashMap<String,String>")
        ),
        outputs = listOf(
//            Output("resultMap", "java.util.HashMap<String,String>")
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
            }, "me" to "zark", "resultMap" to map))
        Assertions.assertTrue(result.isSuccess())
        println(map)
    }
}