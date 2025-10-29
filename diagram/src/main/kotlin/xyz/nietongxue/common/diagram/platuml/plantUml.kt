package xyz.nietongxue.common.diagram.platuml

import net.sourceforge.plantuml.FileFormat
import net.sourceforge.plantuml.FileFormatOption
import net.sourceforge.plantuml.SourceStringReader
import net.sourceforge.plantuml.code.Transcoder
import net.sourceforge.plantuml.code.TranscoderUtil
import xyz.nietongxue.common.base.md5
import xyz.nietongxue.common.base.tempFile
import xyz.nietongxue.common.string.n
import xyz.nietongxue.common.string.wrapBy
import java.awt.Desktop
import java.io.ByteArrayOutputStream
import java.net.URI

fun encode(umlString: String): String {
    val t: Transcoder = TranscoderUtil.getDefaultTranscoder()
    return t.encode(umlString)
}


fun imageDisplay(umlString: String) {

    if (Desktop.isDesktopSupported()) {
        run {
            val desktop = Desktop.getDesktop()
            desktop.browse(toPNG(plantuml(umlString)).tempFile(suffix = "suffix").toURI())
        }
    }
}

fun browser(umlString: String) {

    val uml = plantuml(umlString)

    val url: String = "http://localhost:8080/uml/" + encode(uml)

    if (Desktop.isDesktopSupported()) {
        runCatching {
            val desktop = Desktop.getDesktop()
            desktop.browse(URI(url))
        }
    } else {
        //do nothing
    }
}

sealed interface Uml {


    data class Root(val units: List<Uml>) : Uml {
        init {
            require(units.none {
                it is Root
            })
        }
    }

    /*
    https://plantuml.com/zh/deployment-diagram
     */
    data class Element(val name: String, val type: String = "rectangle", val text: String = name) : Uml

    data class Container(
        val name: String,
        val type: String = "rectangle",
        val text: String = name,
        val units: List<Uml> = emptyList()
    ) : Uml

    /*
    https://plantuml.com/zh/deployment-diagram
     */
    data class Link(
        val from: String,
        val to: String,
        val type: String = "-->",
        val text: String = "",
        val fromToLabel: Pair<String, String> = "" to ""
    ) : Uml

}

fun plantuml(body: String): String {
    return if (body.startsWith("@startuml ")) body else "@startuml" n
            body n
            "@enduml"
}

fun plantuml(uml: Uml): String {
    return plantuml(
        plantumlImpl(uml)
    )
}

fun plantumlImpl(uml: Uml): String {

    fun String.wrapIfNotEmpty(): String {
        return if (this.isNotEmpty()) this.wrapBy("\"") else ""
    }
    return when (uml) {
        is Uml.Element -> "${uml.type} ${uml.text.wrapBy("\"")} as ${uml.name.md5()}"
        is Uml.Link -> "${uml.from.md5()} ${uml.fromToLabel.first.wrapIfNotEmpty()} ${uml.type} ${
            uml.fromToLabel.second.wrapIfNotEmpty()
        } ${uml.to.md5()} : ${uml.text.wrapBy("\"")}"

        is Uml.Root -> uml.units.map { plantumlImpl(it) }.n
        is Uml.Container -> """${uml.type} ${uml.text.wrapBy("\"")} as ${uml.name.md5()} ${
            if (uml.units.isNotEmpty()) "{\n${
                uml.units.map {
                    plantumlImpl(
                        it
                    )
                }.n
            }\n}" else ""
        }"""

        else -> TODO()
    }

}

fun uml(fn: UmlBuilder.() -> Unit): Uml {
    return UmlBuilder().apply(fn).elements.let {
        Uml.Root(it)
    }

}

class UmlBuilder {
    val elements: MutableList<Uml> = mutableListOf()
    fun element(name: String, type: String = "rectangle") {
        elements.add(Uml.Element(name, type))
    }


    fun container(name: String, type: String = "rectangle", fn: UmlBuilder.() -> Unit) {
        elements.add(UmlBuilder().apply(fn).elements.let {
            Uml.Container(name, type = type, units = it)
        })
    }


    fun link(
        from: String,
        to: String,
        type: String = "-->",
        text: String = "",
        fromToLabel: Pair<String, String> = "" to ""
    ) {
        elements.add(Uml.Link(from, to, type = type, text = text, fromToLabel = fromToLabel))
    }


}

fun toSVG(plantUml: String): ByteArray {
    val reader = SourceStringReader(plantUml)
    val os = ByteArrayOutputStream()
    reader.outputImage(os, FileFormatOption(FileFormat.SVG))
    return os.toByteArray()
}

fun toPNG(plantUml: String): ByteArray {
    val reader = SourceStringReader(plantUml)
    val os = ByteArrayOutputStream()
    reader.outputImage(os, FileFormatOption(FileFormat.PNG))
    return os.toByteArray()
}


fun toAscii(plantUml: String): String {
    val reader = SourceStringReader(plantUml)
    val os = ByteArrayOutputStream()
    reader.outputImage(os, FileFormatOption(FileFormat.UTXT))
    return String(os.toByteArray())
}

