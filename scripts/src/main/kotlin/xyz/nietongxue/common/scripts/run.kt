package xyz.nietongxue.common.scripts

import com.alibaba.qlexpress4.Express4Runner
import com.alibaba.qlexpress4.InitOptions
import com.alibaba.qlexpress4.QLOptions
import com.alibaba.qlexpress4.security.QLSecurityStrategy
import com.dashjoin.jsonata.Jsonata
import groovy.lang.GroovyShell
import xyz.nietongxue.common.base.Stuff
import xyz.nietongxue.common.json.JQ_TRANSFER
import xyz.nietongxue.common.json.JQ_TRANSFER_FIRST
import xyz.nietongxue.common.json.autoJson
import xyz.nietongxue.common.json.transform
import kotlin.script.experimental.jsr223.KotlinJsr223DefaultScriptEngineFactory


typealias RunResult = Pair<Any, Stuff>

/// ** 只有一些 facade，主要是把一些依赖封装。
///

object Languages {
    const val ql4 = "ql4"
    const val groovy = "groovy"
    const val kotlin = "kotlin"
    const val jq = "jq"
    const val jqf = "jqf"
}

fun ql4Run(script: String, context: Stuff): RunResult {
    val express4Runner = Express4Runner(InitOptions.builder().securityStrategy(QLSecurityStrategy.open()).build())
    val result = express4Runner.execute(script, context, QLOptions.DEFAULT_OPTIONS) //TODO context不能改变？
    return (result.result to context)
}

fun groovyRun(script: String, context: Stuff): RunResult {
    val engine = GroovyShell().also {
        context.forEach { (string, any) -> it.setVariable(string, any) }
    }
    val result = engine.evaluate(script)
    return (result to context)
}

fun kotlinRun(script: String, context: Stuff): RunResult {
    val engine = KotlinJsr223DefaultScriptEngineFactory().getScriptEngine().also {
        context.forEach { (string, any) -> it.put(string, any) }
    }
    val result = engine.eval(script)
    return (result to context)

}

fun jq(script: String, context: Stuff): RunResult {
    return transform(autoJson(context)!!, script, JQ_TRANSFER) to context
}

/**
 * jqf，自动去 jq 的第一个结果。
 */
fun jqf(script: String, context: Stuff): RunResult {
    return transform(autoJson(context)!!, script, JQ_TRANSFER_FIRST) to context
}


fun jsonata(script: String, context: Stuff): RunResult {
    return run {
        val expr = Jsonata.jsonata(script)
        expr.evaluate(context) to context
    }
}

