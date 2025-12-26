package xyz.nietongxue.common.scripts

import com.alibaba.qlexpress4.Express4Runner
import com.alibaba.qlexpress4.InitOptions
import com.alibaba.qlexpress4.QLOptions
import com.alibaba.qlexpress4.security.QLSecurityStrategy
import xyz.nietongxue.common.base.Stuff
import kotlin.script.experimental.jsr223.KotlinJsr223DefaultScriptEngineFactory

typealias RunResult = Pair<Any, Stuff>


fun ql4Run(script: String, context: Stuff, type: String = "ql4"): RunResult {
    val express4Runner = Express4Runner(InitOptions.builder().securityStrategy(QLSecurityStrategy.open()).build())
    val result = express4Runner.execute(script, context, QLOptions.DEFAULT_OPTIONS) //TODO context不能改变？
    return (result.result to context)
}

fun groovyRun(script: String, context: Stuff): RunResult {
    val engine = groovy.lang.GroovyShell().also {
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