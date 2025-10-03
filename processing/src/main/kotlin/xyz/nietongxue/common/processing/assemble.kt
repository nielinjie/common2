package xyz.nietongxue.common.processing

import xyz.nietongxue.common.log.Log


interface AssemblePhase {
    object Finish : AssemblePhase
}


open class Assemble<V, L>(
    value: V,
    private val rules: List<Rule<V, L>>,
    private val onFinish: List<Reducer<V, L>> = emptyList()
) {
    private var current: ProcessingWithLog<V, L> = bind(value)
    private var finished: Boolean = false
    fun apply(vararg actions: Action<V, L>): Assemble<V, L> {
        actions.forEach {
            apply(it.action())
        }
        return this
    }


    fun applyInScope(fn: ProcessingScope<Log<L>, StopResult<L>, V>.(V) -> Unit): Assemble<V, L> {
        current = current.flatMap { v ->
            val re = ProcessingScope<Log<L>, StopResult<L>, V>()
            re.also {
                it.fn(v)
            }.done()
        }
        return this
    }

    fun apply(reducer: Reducer<V, L>): Assemble<V, L> {
        current = current.flatMap {
            reducer(it)
        }
        return this
    }

    fun finish(): ProcessingWithLog<V, L> {
        require(!finished)
        onFinish.forEach {
            this.apply(it)
        }
        rules.forEach { rule ->
            current = current.flatMap {
                rule.check(it, AssemblePhase.Finish)
            }
        }
        finished = true
        return current
    }

    fun current(): ProcessingWithLog<V, L> {
        return current
    }
}

interface Rule<V, L> {
    fun check(value: V, phase: AssemblePhase): ProcessingWithLog<V, L>
}