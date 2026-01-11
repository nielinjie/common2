package xyz.nietongxue.common

import xyz.nietongxue.common.base.dataUri
import xyz.nietongxue.common.base.parseDataUri
import kotlin.test.Test
import kotlin.text.Charsets.UTF_8

class DataUriTest {
    @Test
    fun test() {
        val bytes = "hello world".toByteArray(UTF_8)
        val dataUri = bytes.dataUri("text/plain")
        println(dataUri)
        val parsed = dataUri.parseDataUri()
        println(parsed.first)
        println(parsed.second.toString(UTF_8))
    }
}