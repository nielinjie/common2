package xyz.nietongxue.common.runtime.callable

import com.fasterxml.jackson.databind.node.ObjectNode
import xyz.nietongxue.common.http.CallOption
import xyz.nietongxue.common.json.JsonWithType
import xyz.nietongxue.common.json.jo





/**
 * RuntimeCallable 是一个可运行部件的声明。
 * 运行时环境负责运行 RuntimeCallable。
 * @see xyz.nietongxue.common.runtime.run.runIt
 */


@JsonWithType
interface RuntimeCallable


data class URLRuntime(
    val url: String,
    val option: CallOption = CallOption()
) : RuntimeCallable


data class ScriptRuntime(
    val script: String,
    val language: String,
) : RuntimeCallable

data class BeanRuntime(
    val className: String,
    val methodName: String,
    val setupArgs: ObjectNode = jo(),
    val setupMethod: String = "setup"
) : RuntimeCallable

data class ConstantRuntime(val returnValue: String) : RuntimeCallable

/**
 * 预设callable，runtime 环境中已经有了。
 */
data class PresetRuntime(val name: String) : RuntimeCallable


/**
 * 一个组合式的callable。
 */
data class ARoundRuntimeCallable(val main: RuntimeCallable, val before: RuntimeCallable?, val after: RuntimeCallable?) :
    RuntimeCallable


/**
 * TODO 整合一个声明式的流程，再加一个流程式引擎。
 */