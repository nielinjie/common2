package xyz.nietongxue.common.processing

typealias Reducer<V,L> = (V) -> ProcessingWithLog<V,L>
interface Action<V,L> {
    fun action(): Reducer<V,L>
}

