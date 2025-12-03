package xyz.nietongxue.common.bpmn

import xyz.nietongxue.common.json.JsonWithType

@JsonWithType
interface ElementOrDefine

@JsonWithType
interface Define:ElementOrDefine {
    fun generate(): Element
}

@JsonWithType
interface Element: ElementOrDefine

interface Event : Element

interface Activity : Element

interface Gateway : Element

interface Flow : Element

@JsonWithType
interface Action {
    //目前可以以有默认值的 inputs 作为 property的模拟。
    data class Input(val name: String, val type: String, val contextVarName: String?, val defaultValue: String? = null)
    data class Output(val name: String, val type: String, val contextVarName: String)
}

data class Process(
    val name: String,
    val namespace: String,
    val elements: List<ElementOrDefine>,
    val inputs: List<Input>, val outputs: List<Output>
) //


data class Task(val name: String, val action: Action) : Activity
data class StartEvent(val name: String) : Event
data class EndEvent(val name: String) : Event
data class SequenceFlow(val name: String, val from: String, val to: String) : Flow
data class Input(val name: String, val type: String)
data class Output(val name: String, val type: String)


interface HasIO {
    val inputs: List<Action.Input>
    val outputs: List<Action.Output>
}

data class ObjectMethodAction(
    val clazz: String, val method: String,
    override val inputs: List<Action.Input>,
    override val outputs: List<Action.Output>,
) : Action, HasIO

data class SpringBeanAction(
    val beanName: String,
    val clazz: String,
    val method: String,
    override val inputs: List<Action.Input>,
    override val outputs: List<Action.Output>
) : Action, HasIO


//Supported types: [java, spring-bean, ql, mvel, groovy, java-inline, java-source]
data class ScriptAction(
    val language: String,
    val script: String,
    override val inputs: List<Action.Input>,
    override val outputs: List<Action.Output>
) : Action, HasIO

