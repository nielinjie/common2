package xyz.nietongxue.common.bpmn

import org.springframework.stereotype.Component

open class MyAdd {
    fun add(a: Int, b: Int): Int {
        return a + b
    }
}

open class MyAppender {
    fun append(a: String, b: String,i: Int): String {
        return "$a append $b".also {
            println(it)
        }
    }
}