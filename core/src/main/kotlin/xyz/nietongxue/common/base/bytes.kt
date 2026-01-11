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

fun String.parseDataUri(): Pair<String, ByteArray> {
    val dataUri = this
    if (!dataUri.startsWith("data:")) {
        throw IllegalArgumentException("Invalid Data URI: must start with 'data:'")
    }

    val commaIndex = dataUri.indexOf(',')
    if (commaIndex == -1) {
        throw IllegalArgumentException("Invalid Data URI: no comma found")
    }

    // 解析 MIME 类型
    val header = dataUri.substring(5, commaIndex)
    fun parseMimeType(header: String): String {
        return if (header.contains(";")) {
            header.substringBefore(";")
        } else {
            header
        }.ifEmpty { "text/plain" }
    }

    val mimeType = parseMimeType(header)


    // 解析数据部分
    val encodedData = dataUri.substring(commaIndex + 1)
    val data = if (header.endsWith(";base64")) {
        encodedData.decodeBase64()
    } else {
        error("Invalid Data URI: no base64 encoding")
    }
    return mimeType to data
}