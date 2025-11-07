package xyz.nietongxue.common.taskdag.stringEvent

import xyz.nietongxue.common.collections.nestedGet
import xyz.nietongxue.common.collections.nestedPut
import xyz.nietongxue.common.collections.toAllLevelMutable
import xyz.nietongxue.common.taskdag.Context
import xyz.nietongxue.common.taskdag.TasksRuntime
import xyz.nietongxue.common.taskdag.stringEvent.CommonEvents.START


fun TasksRuntime<String>.startWithInputs(inputs: Map<String, Any>) {
    this.start(START, mapOf(CommonVars.INPUTS to inputs))
}

fun Context.inputs(name: String): Any? {
    return this.nestedGet(listOf(CommonVars.INPUTS, name))
}

fun Context.taskOutputs(taskName: String): Any? {
    return this.nestedGet(listOf(CommonVars.TASK_OUTPUTS, taskName))
}

fun Context.setTaskOutputs(taskName: String, value: Any): Context {
    return this.toAllLevelMutable().also { it.nestedPut(listOf(CommonVars.TASK_OUTPUTS, taskName), value) }
}

fun Context.outputs(name: String): Any? {
    return this.nestedGet(listOf(CommonVars.OUTPUTS, name))
}

fun Context.setOutputs(name: String, value: Any): Context {
    return this.toAllLevelMutable().also { it.nestedPut(listOf(CommonVars.OUTPUTS, name), value) }
}

fun Context.setInputs(name: String, value: Any): Context {
    return this.toAllLevelMutable().also { it.nestedPut(listOf(CommonVars.INPUTS, name), value) }
}

fun Context.setException(value: Any): Context {
    return this.toAllLevelMutable().also { it.nestedPut(listOf(CommonVars.EXCEPTION), value) }
}


object CommonEvents {
    const val EXCEPTION = "exception"
    const val SUCCESS = "success"
    const val START = "start"
}

object CommonNodes {
    const val DONE = "done"
    const val FAIL = "fail"
    const val INIT = "init"
}

object CommonVars {
    const val EXCEPTION = "exception"
    const val INPUTS = "inputs"
    const val TASK_OUTPUTS = "task_outputs"
    const val OUTPUTS = "outputs"
}