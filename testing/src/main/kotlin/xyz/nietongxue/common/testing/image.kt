package xyz.nietongxue.common.testing

import xyz.nietongxue.common.base.tempFile
import java.awt.Desktop
import java.awt.GraphicsEnvironment

fun openImage(byteArray: ByteArray) {
    if (GraphicsEnvironment.isHeadless()) {
        System.setProperty("java.awt.headless", "false")
    }
    byteArray.tempFile(suffix = "png").toURI().let {
        Desktop.getDesktop().browse(it)
    }
}