package xyz.nietongxue.common.taskdag

import xyz.nietongxue.common.taskdag.stringEvent.*
import xyz.nietongxue.common.taskdag.stringEvent.CommonEvents.EXCEPTION
import xyz.nietongxue.common.taskdag.stringEvent.CommonEvents.START
import xyz.nietongxue.common.taskdag.stringEvent.CommonEvents.SUCCESS
import xyz.nietongxue.common.taskdag.stringEvent.CommonNodes.FAIL
import kotlin.test.Test

/*
作为一个流程使用。
 */

class CommonSeqTasksTest {

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
                SUCCESS to context.setOutputs("useInputs", "hello, $input")
            })

            action("useOutput",action={context ->
                val output = context.outputs("useInputs").also {
                    println("outputs: $it")
                }
                SUCCESS to context.setOutputs("useOutput", "ping, $output")
            })
            init().to(
                "task1"
            ).on(START)
            "task1".to("task2").on("1_2")
            "task2".to("useInputs").on(SUCCESS)
            "useInputs".to("useOutput").on(SUCCESS)
            "useOutput".to(end()).on(SUCCESS)
        }
        val runtime = TasksRuntime(dag)
        runtime.startWithInputs( mapOf("name" to "Alice"))
        runtime.waitForEnd().also {
            println(it.outputs("useOutput"))
        }
    }
    @Test
    fun testStart() {
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
                SUCCESS to context.setOutputs("useInputs", "hello, $input")
            })

            action("useOutput",action={context ->
                val output = context.outputs("useInputs").also {
                    println("outputs: $it")
                }
                SUCCESS to context.setOutputs("useOutput", "ping, $output")
            })
            startFrom("task1")
            "task1".to("task2").on("1_2")
            "task2".to("useInputs").on(SUCCESS)
            "useInputs".to("useOutput").on(SUCCESS)
            "useOutput".to(end()).on(SUCCESS)
        }
        val runtime = TasksRuntime(dag)
        runtime.startWithInputs( mapOf("name" to "Alice"))
        runtime.waitForEnd().also {
            println(it.outputs("useOutput"))
        }
    }
    @Test
    fun testException() {
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
                EXCEPTION to context.setException("exception - $input")
            })

            action("useOutput",action={context ->
                val output = context.outputs("useInputs").also {
                    println("outputs: $it")
                }
                SUCCESS to context.setOutputs("useOutput", "ping, $output")
            })
            startFrom("task1")
            defaultCatching(FAIL)
            "task1".to("task2").on("1_2")
            "task2".to("useInputs").on(SUCCESS)
            "useInputs".to("useOutput").on(SUCCESS)
            "useOutput".to(end()).on(SUCCESS)
        }
        val runtime = TasksRuntime(dag)
        runtime.startWithInputs( mapOf("name" to "Alice"))
        runtime.waitForEnd().also {
            println(it)
        }
    }
}