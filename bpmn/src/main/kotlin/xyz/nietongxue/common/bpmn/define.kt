package xyz.nietongxue.common.bpmn

import com.fasterxml.jackson.annotation.JsonIgnore
import xyz.nietongxue.common.json.JsonWithType

@JsonWithType
interface ElementOrDefine

@JsonWithType
interface Define : ElementOrDefine {
    fun generate(): List<Element>
}

@JsonWithType
interface Element : ElementOrDefine {
    val name: String
}

interface Event : Element

interface Activity : Element

interface Gateway : Element

interface Flow : Element

@JsonWithType
interface Action : ActionOrDefine {
    //目前可以以有默认值的 inputs 作为 property的模拟。
    data class Input(
        val name: String,
        val type: String,
        val contextVarName: String? = null,
        val defaultValue: String? = null
    )

    data class Output(val name: String, val type: String, val contextVarName: String)
}

interface ActionDefine : ActionOrDefine {
    fun generate(): Action
}

@JsonWithType
interface ActionOrDefine
data class Process(
    val name: String,
    val namespace: String,
    val elements: List<ElementOrDefine>,
    val inputs: List<Input> = emptyList(),
    val outputs: List<Output> = emptyList(),
    val innerVars: List<InnerVar> = emptyList()
) {
    @JsonIgnore
    fun getInnerProcess(): InnerProcess {
        return InnerProcess(name, elements)
    }
}//

data class InnerProcess(val name: String, val elements: List<ElementOrDefine>)


data class Task(override val name: String, val action: ActionOrDefine) : Activity
data class StartEvent(override val name: String) : Event
data class EndEvent(override val name: String) : Event
data class SequenceFlow(override val name: String, val from: String, val to: String) : Flow
data class Input(val name: String, val type: String)
data class Output(val name: String, val type: String)
data class InnerVar(val name: String, val type: String)

data class Loop(
    override val name: String, val collectionName: String,
    val itemVarName: String,
    val indexVarName: String,
    val itemClazz: String, val subProcess: InnerProcess
) : Element

interface HasIO {
    val inputs: List<Action.Input>
    val outputs: List<Action.Output>
}

data class ObjectMethodAction(
    val clazz: String, val method: String,
    override val inputs: List<Action.Input> = emptyList(),
    override val outputs: List<Action.Output> = emptyList(),
) : Action, HasIO

data class SpringBeanAction(
    val beanName: String,
    val clazz: String,
    val method: String,
    override val inputs: List<Action.Input> = emptyList(),
    override val outputs: List<Action.Output> = emptyList()
) : Action, HasIO


//Supported types: [java, spring-bean, ql, mvel, groovy, java-inline, java-source]
data class ScriptAction(
    val language: String,
    val script: String,
    override val inputs: List<Action.Input> = emptyList(),
    override val outputs: List<Action.Output> = emptyList()
) : Action, HasIO

