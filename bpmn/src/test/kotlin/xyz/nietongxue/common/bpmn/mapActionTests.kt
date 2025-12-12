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
class MapActionTests {

    @Autowired
    lateinit var engine: ProcessEngine<BpmnModel>

    val process = Process(
        "test", "test", listOf(
            StartEvent("start"),
            SequenceFlow("flow1", "start", "mapping"),
            Task(
                "mapping",
                MapAction(
                    targetClazz = "xyz.nietongxue.common.bpmn.MyAppender2",
                    targetMethod = "append",
                    collection = "collection",
                    resultContextName = "resultMap",
                    byPassVar = "me"
                )
            ),
            SequenceFlow("flow2", "mapping", "end"),
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
            InnerVar("inner_result", "java.lang.String") //TODO 如何去掉这个东西？而且可能在并行的时候出错。
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
                mapOf(
                    "collection" to ArrayList<String>().also {
                        it.add("alice")
                        it.add("bob")
                    },
                    "me" to "zark",
                )
            )
        Assertions.assertTrue(result.isSuccess())
        println(result.data)
    }
}