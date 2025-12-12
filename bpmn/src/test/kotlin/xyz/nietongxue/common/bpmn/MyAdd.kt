package xyz.nietongxue.common.bpmn

import org.springframework.stereotype.Component

open class MyAdd {
    fun add(a: Int, b: Int): Int {
        return a + b
    }
}

open class MyAppender {
    fun append(i: Int, a: String, b: String): String {
        return "No. $i: $a append $b"
    }
}

open class MyAppender2 {
    fun append(a: String, b: String, i: Integer): String {
        return "No. $i: $a append $b"
    }

    fun append(a: String, b: String): String {
        return "No. $a append $b"
    }
}