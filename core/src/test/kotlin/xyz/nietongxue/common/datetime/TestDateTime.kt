package xyz.nietongxue.common.datetime

import java.time.ZoneOffset
import kotlin.test.Test

class TestDateTime {
    @Test
    fun testParse(){
        val string = "Wed, 14 Jan 2026 09:42:35 GMT"
        val re = parseHttpDate( string,)
        println(re)
        println(re.toString())
    }
    @Test
    fun testParseLocal(){
        val string = "Wed, 14 Jan 2026 09:42:35 GMT"
        val re = parseHttpDate( string, ZoneOffset.systemDefault())
        println(re)
        println(re.toString())
    }
}