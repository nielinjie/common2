package xyz.nietongxue.common.string

import kotlin.test.Test

class StringTest {
    @Test
    fun subs(){
        val s = "1234567890"
        s.subStringList().also {
            assert(it.size==10)
            assert(it[0]=="1")
        }
        val s2 = ""
        s2.subStringList().also {
            assert(it.size==0)
        }
        val s3 = "1"
        s3.subStringList().also {
            assert(it.size==1)
            assert(it[0]=="1")
        }
    }
}