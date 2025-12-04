package xyz.nietongxue.common.bpmn

class MapTask(
    val resultContextName: String,
    val firstTaskName: String,
    val lastTaskName: String,
    val localVarName:String,
    val action: Action
) : Define {
    val innerSuffix: String = "inner_${this.hashCode()}"
    val inner = InnerProcess(
        name = "process_$innerSuffix",
        elements = listOf(
            StartEvent("start_$innerSuffix"),
            SequenceFlow("flow2_$innerSuffix", "start_$innerSuffix", "task1_$innerSuffix"),
            Task(
                "task1_$innerSuffix", action
            ),
            SequenceFlow(
                "flow3_$innerSuffix", "task1_$innerSuffix", "setResult"
            ),
            Task(
                name = "setResult", ScriptAction(
                    "groovy", "resultMap.put(i,inner_result);",
                    inputs = listOf(
                        Action.Input("i", "java.lang.Integer", "i"),
                        Action.Input(localVarName, "java.lang.String", localVarName),
                        Action.Input(resultContextName, "java.util.HashMap<String,String>", resultContextName)
                    ),
                )
            ),
            SequenceFlow(
                "flow4_$innerSuffix", "setResult", "end_$innerSuffix"
            ),
            EndEvent("end_$innerSuffix"),
        )
    )

    override fun generate(): List<Element> = listOf(
        Task(
            firstTaskName, ScriptAction(
                "groovy",
                "new HashMap<String,String>()",
                outputs = listOf(
                    Action.Output(resultContextName, "java.util.HashMap<String,String>", resultContextName)
                ),
            )
        ),
        SequenceFlow("flow15", firstTaskName, lastTaskName),
        Loop(lastTaskName, "collection", "p", "i", "java.lang.String", inner),
    )
}