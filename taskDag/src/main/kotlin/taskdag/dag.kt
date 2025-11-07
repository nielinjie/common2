package xyz.nietongxue.common.taskdag

import java.util.Collections.emptyList

/*
DAG 的一部分，有时候在DAG 通过几个 Graph 构成。
 */
class TaskGraph<E : Any>(
    val tasks: List<Task<E>> = emptyList(),
    val trans: List<Trans<E>> = emptyList()
)


data class TaskDAG<E : Any>(
    val tasks: List<Task<E>> = emptyList(),
    val trans: List<Trans<E>> = emptyList()
) {
    //will be call just before start
    fun validate(): Result<Unit> {
        return runCatching {
            val init = tasks.filter { it is InitTask<*> }
            val ends = tasks.filter { it is EndTask<*> }
            require(init.size == 1) { "init task should be exactly one, but got - $init" }
            require(ends.isNotEmpty()) { "end task should be at least one, but got - $ends" }

            val names = tasks.map { it.name }
            require(names.size == names.toSet().size) {
                "task name should be unique"
            }
            require(trans.toSet().size == trans.size) {
                "trans should be unique"
            }
            require(trans.all {
                it.from in names && it.to in names
            }) {
                "trans should be in tasks"
            }
        }
    }

    fun startEvent(): Result<E> {
        val init = tasks.first { it is InitTask<*> }
        return kotlin.runCatching {
            trans.filter { it.from == init.name }.let {
                require(it.size == 1) { "multi start event found - ${it.map { it.event }}" }
                it.first().event
            }
        }
    }

    fun normalTasks(): List<Task<E>> {
        return tasks.filter { it !is InitTask<*> && it !is EndTask<*> }
    }
}


fun <E : Any> TaskDAG<E>.plus(taskGraph: TaskGraph<E>): TaskDAG<E> {
    return TaskDAG(
        tasks = tasks + taskGraph.tasks,
        trans = trans + taskGraph.trans
    )
}

fun <E : Any> TaskDAG<E>.plus(task: Task<E>): TaskDAG<E> {
    return this.copy(
        tasks = tasks + task,
    )
}


fun <E : Any> TaskDAG<E>.toGraph(): TaskGraph<E> {
    return TaskGraph(
        tasks = tasks,
        trans = trans
    )
}
