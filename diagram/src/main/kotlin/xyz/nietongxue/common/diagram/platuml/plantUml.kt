package xyz.nietongxue.common.diagram.platuml

import net.sourceforge.plantuml.FileFormat
import net.sourceforge.plantuml.FileFormatOption
import net.sourceforge.plantuml.SourceStringReader
import net.sourceforge.plantuml.code.Transcoder
import net.sourceforge.plantuml.code.TranscoderUtil
import xyz.nietongxue.common.base.md5
import xyz.nietongxue.common.base.tempFile
import xyz.nietongxue.common.json.JsonWithType
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

@JsonWithType
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
    data class Element(
        val name: String,
        val type: String = "rectangle",
        val text: String = name,
        val stereotype: String? = null
    ) : Uml

    data class Container(
        val name: String,
        val type: String = "rectangle",
        val text: String = name,
        val stereotype: String? = null,
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
        is Uml.Element -> "${uml.type} ${uml.text.wrapBy("\"")} as ${uml.name.md5()} ${
            uml.stereotype?.let { "<<$it>>" }.orEmpty()
        }"

        is Uml.Link -> "${uml.from.md5()} ${uml.fromToLabel.first.wrapIfNotEmpty()} ${uml.type} ${
            uml.fromToLabel.second.wrapIfNotEmpty()
        } ${uml.to.md5()} : ${uml.text.wrapBy("\"")}"

        is Uml.Root -> uml.units.map { plantumlImpl(it) }.n
        is Uml.Container -> """${uml.type} ${uml.text.wrapBy("\"")} as ${uml.name.md5()} ${
            uml.stereotype?.let { "<<$it>>" }.orEmpty()
        } ${
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

fun uml(fn: UmlElementsBuilder.() -> Unit): Uml {
    return UmlElementsBuilder().apply(fn).elements.let {
        Uml.Root(it)
    }

}

class AppearanceBuilder {
    var stereotype: String? = null
    var text: String? = null
    var labelAtHead: Pair<String, String>? = null
}

class UmlElementsBuilder {
    val elements: MutableList<Uml> = mutableListOf()
    fun element(name: String, type: String = "rectangle", appearanceBuilding: AppearanceBuilder.() -> Unit = {}) {
        val appear = AppearanceBuilder().apply(appearanceBuilding)
        elements.add(Uml.Element(name, type, stereotype = appear.stereotype, text = appear.text ?: name))
    }


    fun container(
        name: String,
        type: String = "rectangle",
        appearanceBuilding: AppearanceBuilder.() -> Unit = {},
        fn: UmlElementsBuilder.() -> Unit
    ) {
        elements.add(UmlElementsBuilder().apply(fn).elements.let {
            val appear = AppearanceBuilder().apply(appearanceBuilding)
            Uml.Container(name, text = appear.text ?: name, type = type, units = it, stereotype = appear.stereotype)
        })
    }


    fun link(
        from: String,
        to: String,
        type: String = "-->",
        appearanceBuilding: AppearanceBuilder.() -> Unit ={}
    ) {
        val appear = AppearanceBuilder().apply(appearanceBuilding)
        elements.add(
            Uml.Link(
                from,
                to,
                type = type,
                text = appear.text ?: "",
                fromToLabel = appear.labelAtHead ?: ("" to "")
            )
        )
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

