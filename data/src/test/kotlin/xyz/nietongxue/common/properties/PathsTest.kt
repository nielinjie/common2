package xyz.nietongxue.common.properties

import org.assertj.core.api.Assertions.assertThat
import xyz.nietongxue.common.softdata.path.PathNode
import xyz.nietongxue.common.softdata.path.paths
import kotlin.test.Test


class PathsTest {

    @Test
    fun test() {
        val property = ObjectProperty(mapOf("userName" to SimpleProperty("string")))
        val get = property.get(listOf(PathNode.NameNode("userName")))
        println(get)
    }

    @Test
    fun test2() {
        val property = ObjectProperty(mapOf("userName" to SimpleProperty("string")))
        val get = property.get(
            listOf(
                PathNode.NameNode("owner"),
                PathNode.NameNode("userName")
            )
        )
        println(get)
        val get2 = property.get(
            listOf(PathNode.NameNode("owner"))
        )
        println(get2)
    }

    @Test
    fun test3() {
        val property = ObjectProperty(
            mapOf(
                "users" to
                        ArrayProperty(
                            ObjectProperty(mapOf("userName" to SimpleProperty("string")))
                        )
            )
        )
        val get2 = property.get(
            listOf(
                PathNode.NameNode("users"),
                PathNode.AllItemNode,
                PathNode.NameNode("userName")
            )
        )
        println(get2)
    }

    @Test
    fun test4() {
        val property = ObjectProperty(
            mapOf(
                "users" to
                        ArrayProperty(
                            ObjectProperty(mapOf("userName" to SimpleProperty("string")))
                        )
            )
        )
        val paths = property.paths()
        println(paths)
    }

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
        val paths = property.paths()
        println(paths)
        paths.forEach {
            val gotProperty = property.get(it.path)
            assertThat(it.property).isEqualTo(gotProperty)
        }
    }
}