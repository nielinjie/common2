package xyz.nietongxue.common.properties

import com.jayway.jsonpath.JsonPath
import org.assertj.core.api.Assertions.assertThat
import xyz.nietongxue.common.json.JPathConfig
import xyz.nietongxue.common.softdata.DataStructure
import xyz.nietongxue.common.softdata.TransformPath
import xyz.nietongxue.common.softdata.Transforming
import xyz.nietongxue.common.softdata.building
import xyz.nietongxue.common.softdata.paths
import xyz.nietongxue.common.softdata.transform
import xyz.nietongxue.common.testing.pretty
import kotlin.test.Test

class TransformTest {
    @Test
    fun test5() {
        val property = ObjectProperty(
            mapOf(
                "users" to
                        ArrayProperty(
                            ObjectProperty(mapOf("userName" to SimpleProperty("string")))
                        )
            )
        )
        val new = property.transform(
            TransformPath.parse("$.users.[]"), { oldProperty ->
                SimpleProperty("string").building()
            }
        )
        new.paths().forEach {
            println(it)
        }
    }

    @Test
    fun test4() {
        JPathConfig()
        val property = ObjectProperty(
            mapOf(
                "users" to
                        ArrayProperty(
                            ObjectProperty(mapOf("userName" to SimpleProperty("string")))
                        )
            )
        )
        val data = DataStructure(property).also {
            it.value = mapOf(
                "users" to listOf(
                    mapOf("userName" to "xiaoming")
                )
            )
        }
        val transforming = Transforming(TransformPath.parse("$.users.[]"), { oldProperty ->
            SimpleProperty("string").building()
        }, {
            (it as Map<String, String>)["userName"]
        })
        val new = data.transform(transforming)
        println(new.value!!)
    }

    @Test
    fun test3() {
        JPathConfig()
        val property = ObjectProperty(
            mapOf(
                "users" to
                        ArrayProperty(
                            ObjectProperty(mapOf("userName" to SimpleProperty("string")))
                        )
            )
        )
        val data = DataStructure(property).also {
            it.value = mapOf(
                "users" to listOf(
                    mapOf("userName" to "xiaoming")
                )
            )
        }
        val transforming = Transforming(TransformPath.parse("$.users"), { oldProperty ->
            SimpleProperty("string").building()
        }, {
            ((it as List<Map<String, String>>).get(0))["userName"]
        })
        val new = data.transform(transforming)
        println(new.value!!)
    }

    @Test
    fun test2() {
        JPathConfig()
        val property = ObjectProperty(
            mapOf(
                "users" to
                        ArrayProperty(
                            ObjectProperty(mapOf("userName" to SimpleProperty("string")))
                        )
            )
        )
        val data = DataStructure(property).also {
            it.value = mapOf(
                "users" to listOf(
                    mapOf("userName" to "lei li"),
                    mapOf("userName" to "meimei han")
                )
            )
        }
        val transforming = Transforming(TransformPath.parse("$.users.[].userName"), { oldProperty ->
            ObjectProperty(
                mapOf(
                    "first" to SimpleProperty("string"), "last" to SimpleProperty("string")
                )
            ).building()
        }, {
            (it as String).let {
                val (first, last) = it.split(" ")
                mapOf("first" to first, "last" to last)
            }
        })
        val new = data.transform(transforming)
        println(new.value!!)
    }
}