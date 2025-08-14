package xyz.nietongxue.common.persistence

import xyz.nietongxue.common.base.Path
import java.nio.file.Files
import java.util.stream.Collectors
import kotlin.io.path.div
import kotlin.io.path.pathString
import java.nio.file.Path as FPath

typealias FileMapping = Pair<Path, FPath>


class PathMapToFileTree(
    val base: FPath,
    val matchFileName: String,
) {
    fun listPath(fileName: String = matchFileName): List<FileMapping> = searchDir(base, fileName)
//    fun searchPath(path: Path): FileMapping? = listPath().find { it.first == path } //TODO 增加部分匹配模式。

    fun listPath(prefix: Path, fileName: String = matchFileName): List<FileMapping> =
        listPath(fileName).filter { it.first.startsWith(prefix) } //TODO 增加部分匹配模式。

    fun getFPath(path: Path, fileName: String = matchFileName): FPath =
        (base / kotlin.io.path.Path(path.asString()) / fileName).also {
            Files.createDirectories(it.parent)
        }

    fun listFPath(): List<FPath> = listPath().map {
        getFPath(it.first).toAbsolutePath()
    }

    fun searchDir(base: FPath, fileName: String, excludeSuffixDirLevel: Int = 0): List<FileMapping> {
        return listFilesRecursively(base).mapNotNull {
            if (it.fileName.toString() == fileName) {
                it.toPath(base, excludeSuffixDirLevel) to it
            } else {
                null
            }
        }
    }

    fun FPath.toPath(base: FPath, excludeSuffixDirLevel: Int = 0): Path {
        return Path.fromString(base.relativize(this).parent.pathString).drop(excludeSuffixDirLevel)
    }

}

fun listFilesRecursively(baseDir: FPath): List<FPath> {
    return Files.walk(baseDir)
        .filter { Files.isRegularFile(it) }.collect(Collectors.toList())
}


