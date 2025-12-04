package xyz.nietongxue.common.bpmn


fun elements(block: ElementsBuilder.() -> Unit): List<Element> {
    val builder = ElementsBuilder().also(block)
    return builder.elements.flatMap {
        when (it) {
            is Element -> listOf(it)
            is Define -> it.generate()
            else -> error("unsupported type")
        }
    }
}

class ElementsBuilder {
    val elements: MutableSet<ElementOrDefine> = mutableSetOf()

    fun Element.connect(b: Element) : Element{
        elements.add(this)
        elements.add(b)
        elements.add(SequenceFlow("flow_${this.hashCode()}_${b.hashCode()}", this.name, b.name))
        return b
    }

    fun ElementOrDefine.connect(b: ElementOrDefine) : ElementOrDefine{
        val from: Element = when (this) {
            is Element -> this
            is Define -> this.generate().last()
            else -> error("unsupported type")
        }
        val to: Element = when (b) {
            is Element -> b
            is Define -> b.generate().first()
            else -> error("unsupported type")
        }
        return from.connect(to)
    }

    fun start(): Element {
        return StartEvent("start").also {
            elements.add(it)
        }
    }

    fun end(): Element {
        return EndEvent("end").also {
            elements.add(it)
        }
    }
}