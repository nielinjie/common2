package xyz.nietongxue.common.taskdag

import xyz.nietongxue.common.taskdag.stringEvent.*
import xyz.nietongxue.common.taskdag.stringEvent.CommonEvents.EXCEPTION
import xyz.nietongxue.common.taskdag.stringEvent.CommonEvents.SUCCESS
import xyz.nietongxue.common.taskdag.stringEvent.CommonNodes.FAIL
import kotlin.test.Test

class MultiThreadsTest {

    @Test
    fun test() {

        val dag = dag {
            action("task1") {
                longTimeFunc(2, "task1")
                println("task1 done")
                "1_2" to it
            }

            action("task2") {
                longTimeFunc(1, "task2")
                println("task2 done")
                SUCCESS to it
            }
            action("useInputs", action = { context ->
                val input = context.inputs("name").also {
                    println("inputs: $it")
                }
                if ((input as String).startsWith("B")) {
                    EXCEPTION to context.setException("name is Bxx")
                } else
                    SUCCESS to context.setOutputs("useInputs", "hello, $input")
            })

            action("useOutput", action = { context ->
                val output = context.outputs("useInputs").also {
                    println("outputs: $it")
                }
                SUCCESS to context.setOutputs("useOutput", "ping, $output")
            })
            "task1".to("task2").on("1_2")
            "task2".to("useInputs").on(SUCCESS)
            "useInputs".to("useOutput").on(SUCCESS)
            "useOutput".to(end()).on(SUCCESS)
            startFrom("task1")
            defaultCatching(FAIL)
        }
        val threads = mutableListOf<Thread>()
        val names = listOf("Alice", "Bob", "Charlie", "David", "Eve", "Frank", "Grace", "Helen", "Irene", "Judy")
        val inputs = names.map { name ->
            mapOf("name" to name)
        }
        val delay = listOf(2, 1, 3, 2, 1, 4, 2, 1, 3, 2)
        delay.withIndex().forEach { (i, v) ->
            Thread {
                Thread.sleep(v * 1000L)
                println("thread --$i-- start")
                val runtime = TasksRuntime(dag)
                runtime.startWithInputs(inputs.get(i).plus("no" to i))
                runtime.waitForEnd().also {
                    println(it.outputs("useOutput"))
                    if (it.get(EXCEPTION) != null) {
                        println("thread --$i-- exception")
                    }
                }
                println("thread --$i-- done")
            }.also {
                it.start()
                threads.add(it)
            }
        }
        threads.forEach { it.join() }
        println("All threads completed")
    }

}