package xyz.nietongxue.common.spring.scan

import org.springframework.core.type.filter.AssignableTypeFilter
import kotlin.test.Test

class ScanTest {
    @Test
    fun test() {
        scanPackage(listOf("xyz.nietongxue.common.spring.scan"), AssignableTypeFilter(Any::class.java)
        ).also {
            println(it)
        }
    }
}