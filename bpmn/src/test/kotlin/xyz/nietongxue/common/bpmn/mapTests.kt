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
class MapTests {

    @Autowired
    lateinit var engine: ProcessEngine<BpmnModel>


    val process = Process(
        "test", "test", listOf(
            StartEvent("start"),
            SequenceFlow("flow1", "start", "newResultMap"),
            MapTask(
                resultContextName = "resultMap",
                firstTaskName = "newResultMap",
                lastTaskName = "loop",
                localVarName = "inner_result",
                action = ObjectMethodAction(
                    "xyz.nietongxue.common.bpmn.MyAppender", "append",
                    inputs = listOf(
                        Action.Input("a", "java.lang.String", "p"),
                        Action.Input("b", "java.lang.String", "me"),
                        Action.Input("i", "java.lang.Integer", "i"),
                    ), outputs = listOf(
                        Action.Output("result", "java.lang.String", "inner_result")
                    )
                )
            ),
            SequenceFlow("flow2", "loop", "end"),
            EndEvent("end"),
        ),
        inputs = listOf(
            Input("collection", "java.util.ArrayList<String>"),
            Input("me", "java.lang.String"),
        ),
        outputs = listOf(
            Output("resultMap", "java.util.HashMap<String,String>")
        ),
        innerVars = listOf(
            InnerVar("inner_result", "java.lang.String") //TODO 如何去掉这个东西？
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
            engine.execute(
                ProcessSource.fromContent(code, content),
                mapOf("collection" to ArrayList<String>().also {
                    it.add("alice")
                    it.add("bob")
                }, "me" to "zark", "inner_result" to "??")
            )
        Assertions.assertTrue(result.isSuccess())
        println(result.data)
    }
}