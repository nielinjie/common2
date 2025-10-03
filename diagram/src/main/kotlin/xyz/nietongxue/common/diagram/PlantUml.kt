package xyz.nietongxue.common.diagram

import net.sourceforge.plantuml.FileFormat
import net.sourceforge.plantuml.FileFormatOption
import net.sourceforge.plantuml.SourceStringReader
import net.sourceforge.plantuml.code.Transcoder
import net.sourceforge.plantuml.code.TranscoderUtil
import xyz.nietongxue.common.string.n
import java.awt.Desktop
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.URI
import java.net.URISyntaxException

fun encode(umlString: String): String {
    val t: Transcoder = TranscoderUtil.getDefaultTranscoder()
    return t.encode(umlString)
}

fun browser(umlString: String) {

    val uml = if (!umlString.startsWith("@startuml")) {
        "@startuml\n$umlString\n@enduml"
    } else umlString

    val url: String = "http://localhost:8080/uml/" + encode(uml)

    if (Desktop.isDesktopSupported()) {
        val desktop = Desktop.getDesktop()
        try {
            desktop.browse(URI(url))
        } catch (e: IOException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
    } else {
        val runtime = Runtime.getRuntime()
        try {
            runtime.exec("xdg-open $url")
        } catch (e: IOException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }
    }
}

sealed interface Uml {


    data class Root(val type: String = "root") : Uml

    data class Element(val type: String, val text: String) : Uml

    data class Container(val type: String, val text: String) : Uml

}

fun plantuml(body: String): String {
    return if (body.startsWith("@startuml ")) body else "@startuml" n
            body n
            "@enduml"
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


