package xyz.nietongxue.common.bpmn

import org.springframework.stereotype.Component

@Component
open class MyAdd {
    fun add(a: Int, b: Int): Int {
        return a + b
    }
}