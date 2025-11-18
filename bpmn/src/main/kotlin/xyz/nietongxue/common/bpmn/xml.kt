package xyz.nietongxue.common.bpmn

import org.redundent.kotlin.xml.Namespace
import org.redundent.kotlin.xml.xml

fun Process.toXML(): String {
    val ns = Namespace("http://www.omg.org/spec/BPMN/20100524/MODEL")
    val ns2 = Namespace("cf","http://www.compileflow.org/schema/1.0")

        return xml("definitions", namespace = ns,) {
            namespace(ns2)
            attribute("targetNamespace", this@toXML.namespace)
        "process" {
            attribute("name", this@toXML.name)
            attribute("id", this@toXML.name)
            attribute("isExecutable", "true")
            "extensionElements" {
                this@toXML.inputs.forEach {
                    "cf:var" {
                        attribute("name", it.name)
                        attribute("description", it.name)
                        attribute("dataType", it.type)
                        attribute("inOutType", "param")
                    }
                }
                this@toXML.outputs.forEach {
                    "cf:var" {
                        attribute("name", it.name)
                        attribute("description", it.name)
                        attribute("dataType", it.type)
                        attribute("inOutType", "return")
                    }
                }
            }
            this@toXML.elements.forEach {
                when (it) {
                    is Task -> {
                        "serviceTask" {
                            attribute("name", it.name)
                            attribute("id", it.name)
                            "extensionElements" {
                                "cf:action" {
                                    attribute("type", "java")
                                    "cf:actionHandle" {
                                        (it.action as ObjectMethodAction).also {
                                            attribute("clazz", it.clazz)
                                            attribute("method", it.method)
                                            it.inputs.forEach {
                                                "cf:var" {
                                                    attribute("name", it.name)
                                                    attribute("dataType", it.type)
                                                    attribute("contextVarName", it.contextVarName)
                                                    attribute("inOutType", "param")
                                                }
                                            }
                                            it.outputs.forEach {
                                                "cf:var" {
                                                    attribute("name", it.name)
                                                    attribute("dataType", it.type)
                                                    attribute("contextVarName", it.contextVarName)
                                                    attribute("inOutType", "return")
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    is StartEvent -> {
                        "startEvent" {
                            attribute("name", it.name)
                            attribute("id", it.name)
                        }
                    }

                    is EndEvent -> {
                        "endEvent" {
                            attribute("name", it.name)
                            attribute("id", it.name)
                        }
                    }

                    is SequenceFlow -> {
                        "sequenceFlow" {
                            attribute("name", it.name)
                            attribute("id", it.name)
                            attribute("sourceRef", it.from)
                            attribute("targetRef", it.to)
                        }
                    }
                }
            }
        }
    }.toString(true)
}