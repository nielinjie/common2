package xyz.nietongxue.common.diagram.platuml

import kotlin.test.Test

class PlantUmlTest {

    @Test
    fun testUP() {
        imageDisplay(
            plantuml(
                """
                class u0cc175b9c0f136a8b1c399e269772661 as "a" 
                class u92eb5ffee6ae3fecbad71c777531578f as "b" 
                u0cc175b9c0f136a8b1c399e269772661 --> u92eb5ffee6ae3fecbad71c777531578f
            """.trimIndent()
            )
        )
    }

    @Test
    fun testU() {
        toAscii(
            plantuml(
                """
                class u0cc175b9c0f136a8b1c399e269772661 as "a" 
                class u92eb5ffee6ae3fecbad71c777531578f as "b" 
                u0cc175b9c0f136a8b1c399e269772661 --> u92eb5ffee6ae3fecbad71c777531578f
            """.trimIndent()
            )
        ).also {
            println(it)
        }
    }


    @Test
    fun test() {
        toAscii(
            plantuml(
                """
                class ab as "A"
                class B
                ab --> B
            """.trimIndent()
            )
        ).also {
            println(it)
        }
    }


    @Test
    fun test2() {
        toAscii(
            plantuml(
                Uml.Root(
                    units =
                        listOf(
                            Uml.Element("a", "class"),
                            Uml.Element("b", "class"),
                            Uml.Link("a", "b"),
                        )
                )
            ).also {
                println(it)
            }
        ).also {
            println(it)
        }
    }

    @Test
    fun test3() {
        plantuml(
            Uml.Root(
                units =
                    listOf(
                        Uml.Element("a", "class"),
                        Uml.Element("b", "class"),
                        Uml.Element("c", "class"),
                        Uml.Link("a", "b"),
                        Uml.Link("a", "c")
                    )
            )
        ).also {
            println(it)
            imageDisplay(it)
        }

    }

    @Test
    fun test4() {
        plantuml(
            Uml.Root(
                units =
                    listOf(
                        Uml.Element("a", "class"),
                        Uml.Element("b", "class"),
                        Uml.Element("c", "class"),
                        Uml.Link("a", "b"),
                        Uml.Link("b", "c")
                    )
            )
        ).also {
            println(it)
            imageDisplay(it)
        }

    }

    @Test
    fun testNested() {
        plantuml(
            Uml.Root(
                units =
                    listOf(
                        Uml.Element("a", "component"),
                        Uml.Element("b", "component"),
                        Uml.Container(
                            "c", "component",
                            units = listOf(
                                Uml.Element("d", "component"),
                            )
                        ),
                        Uml.Link("a", "b"),
                        Uml.Link("b", "c"),
                        Uml.Link("a", "d", text = "what?"),
                    )
            )
        ).also {
            println(it)
            imageDisplay(it)
        }

    }

    @Test
    fun testSimple() {
        plantuml(
            Uml.Root(
                units = listOf(
                    Uml.Element("a"),
                    Uml.Element("b"),
                    Uml.Container(
                        "c", units = listOf(
                            Uml.Element("d"),
                        )
                    ),
                    Uml.Element("e"),
                    Uml.Link("a", "b"),
                    Uml.Link("d", "e"),
                )
            )
        ).also {
            println(it)
            imageDisplay(it)
        }
    }

    @Test
    fun testBuilding() {
        plantuml(uml {
            element("a")
            container("b") {
                database("c") {
                    component("e")
                }
            }
            folder("folder") {
                file("file")
            }
            component("component0"){
                component("component2")
            }
            link("a", "e")
            link("file", "e", fromToLabel = "file" to "e")
        }).also {
            println(it)
            imageDisplay(it)
        }
    }
}