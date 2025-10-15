package xyz.nietongxue.common.base

import java.io.File
import java.util.Base64

fun ByteArray.dataUri(mimeType: String): String {
    return "data:$mimeType;base64,${this.base64()}"
}

fun ByteArray.base64(): String {
    return Base64.getEncoder().encodeToString(this)
}


fun ByteArray.tempFile(prefix: String = "any", suffix: String): File {
    val tempFile = File.createTempFile(prefix, suffix).also { it.writeBytes(this) }
    return tempFile
}

fun String.decodeBase64(): ByteArray {
    return Base64.getDecoder().decode(this)
}