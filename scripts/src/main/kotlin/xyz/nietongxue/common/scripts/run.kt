package xyz.nietongxue.common.scripts

import com.alibaba.qlexpress4.Express4Runner
import com.alibaba.qlexpress4.InitOptions
import com.alibaba.qlexpress4.QLOptions
import com.alibaba.qlexpress4.security.QLSecurityStrategy
import com.dashjoin.jsonata.Jsonata
import groovy.lang.GroovyShell
import org.slf4j.Logger
import xyz.nietongxue.common.base.Stuff
import xyz.nietongxue.common.json.JQ_TRANSFER
import xyz.nietongxue.common.json.JQ_TRANSFER_FIRST
import xyz.nietongxue.common.json.autoJson
import xyz.nietongxue.common.json.transform
import java.io.File
import java.net.URLClassLoader
import kotlin.script.experimental.jsr223.KOTLIN_JSR223_RESOLVE_FROM_CLASSLOADER_PROPERTY
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


fun kotlinRun(script: String, context: Stuff, customClassloader: ClassLoader? = null): RunResult {
    System.setProperty(KOTLIN_JSR223_RESOLVE_FROM_CLASSLOADER_PROPERTY, "false")
    val logger = context.get("logger") as? Logger


    val classLoader = Thread.currentThread().contextClassLoader
    logger?.info("thread: ${Thread.currentThread().name}")
    logger?.info("classLoader: ${classLoader.name}")
    val classpath = when (classLoader) {
        is URLClassLoader -> {
            classLoader.urLs.joinToString(File.pathSeparator) { url ->
                File(url.toURI()).absolutePath
            }
        }

        else -> System.getProperty("java.class.path")
    }
    logger?.info("classpath find by thread: $classpath")
//    val setClasspath = "file:/app/plugins/plugin-llm-0.8.0-SNAPSHOT.jar"
//    logger?.info("Script engine classpath set by hand: $setClasspath")

//    val urls = arrayOf(URI(setClasspath).toURL())

    // 2. 创建一个基于当前类加载器的 URLClassLoader
    val currentClassLoader = Thread.currentThread().contextClassLoader
    val customClassLoaderInner = customClassloader ?: currentClassLoader

    // 3. 将其设置为当前线程的上下文类加载器
    Thread.currentThread().contextClassLoader = customClassLoaderInner

    try {
        // 4. 初始化脚本引擎（此时它将加载 customClassLoader 中的类）
        val engine = KotlinJsr223DefaultScriptEngineFactory().getScriptEngine().also {
            context.forEach { (string, any) -> it.put(string, any) }
        }

        val result = engine.eval(script)
        logger?.info("Script engine run result: $result")
        return (result to context)

    } finally {
        // 5. 还原线程的上下文类加载器（推荐做法）
        Thread.currentThread().contextClassLoader = currentClassLoader
    }


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

