package xyz.nietongxue.common.taskdag.stringEvent

import xyz.nietongxue.common.ensure.EnsureResult
import xyz.nietongxue.common.taskdag.DefaultExceptionTrans
import xyz.nietongxue.common.taskdag.DefaultStartNodeTrans
import xyz.nietongxue.common.taskdag.TaskDAGBuilder
import xyz.nietongxue.common.taskdag.stringEvent.CommonEvents.EXCEPTION
import xyz.nietongxue.common.taskdag.stringEvent.CommonEvents.START
import xyz.nietongxue.common.taskdag.stringEvent.CommonNodes.FAIL


fun TaskDAGBuilder<String>.defaultCatching(endTaskName: String = FAIL) {
    this.modifier(DefaultExceptionTrans(endTaskName, EXCEPTION))
}

fun TaskDAGBuilder<String>.startFrom(taskName: String) {
    this.modifier(DefaultStartNodeTrans(taskName, START))
}