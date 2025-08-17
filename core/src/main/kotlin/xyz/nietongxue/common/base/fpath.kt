package xyz.nietongxue.common.base

import kotlin.io.path.pathString
import java.nio.file.Path as FPath


fun FPath.pathAndExtension(): Pair<String, String> {
    val pathString = this.pathString //TODO 保持原貌的string有没有？
    val lastIndex = pathString.lastIndexOf('.')
    return if (lastIndex == -1) {
        pathString to ""

    } else {
        pathString.substring(0, lastIndex) to pathString.substring(lastIndex + 1)
    }
}

fun FPath.fileNameAndExtension(): Pair<String, String> {
    val fileName = this.fileName.toString()
    val lastIndex = fileName.lastIndexOf('.')
    return if (lastIndex == -1) {
        fileName to ""
    } else {
        fileName.substring(0, lastIndex) to fileName.substring(lastIndex + 1)
    }
}

fun FPath.extension(): String {
    return this.fileName.toString().substringAfterLast('.', "")
}

fun FPath.splitTo3(): Triple<String, String, String> {
    val pathString = this.pathString
    val lastIndex = pathString.lastIndexOf('/')
    val first = pathString.substring(0, lastIndex)
    val last = pathString.substring(lastIndex + 1)
    val lastIndex2 = last.lastIndexOf('.')
    val middle = if (lastIndex2 == -1) {
        last
    } else {
        last.substring(0, lastIndex2)
    }
    val last2 = if (lastIndex2 == -1) {
        ""
    } else {
        last.substring(lastIndex2)
    }
    return Triple(first, middle, last2)
}
