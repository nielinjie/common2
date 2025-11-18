package xyz.nietongxue.common.bpmn

import xyz.nietongxue.common.json.JsonWithType


@JsonWithType
interface Element

interface Event : Element

interface Activity : Element

interface Gateway : Element

interface Flow : Element

interface Action {
    data class Input(val name: String, val type: String, val contextVarName: String)
    data class Output(val name: String, val type: String, val contextVarName: String)
}

data class Process(
    val name: String,
    val namespace: String,
    val elements: List<Element>,
    val inputs: List<Input>, val outputs: List<Output>
) //


data class Task(val name: String, val action: Action) : Activity
data class StartEvent(val name: String) : Event
data class EndEvent(val name: String) : Event
data class SequenceFlow(val name: String, val from: String, val to: String) : Flow
data class Input(val name: String, val type: String)
data class Output(val name: String, val type: String)

data class ObjectMethodAction(
    val clazz: String, val method: String, val inputs: List<Action.Input>, val outputs: List<Action.Output>
) : Action