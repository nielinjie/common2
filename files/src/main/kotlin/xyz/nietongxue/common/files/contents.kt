package xyz.nietongxue.common.files

import xyz.nietongxue.common.base.Path
import xyz.nietongxue.common.persistence.FileMapping
import java.nio.file.Path as FPath


/*
    * 把文件系统作为一个内容存储
    * 1. 一个文件一个数据。
    * 2. path 作为数据的某种 key
    * 3. hash 作为识别文件的标准
    * 4. 实现数据改变的监听
 */


data class IdPair(val path: Path, val hash: String)

data class Version(val idPairs: List<IdPair>)

interface FilesChanged {
    data class Added(val path: Path) : FilesChanged
    data class Removed(val path: Path) : FilesChanged
    data class Changed(val path: Path) : FilesChanged
    data class Moved(val from: Path, val to: Path) : FilesChanged
}


//TODO
class FilesBase(val path: FPath) {
//    val
    fun snapshot(): Version {
        TODO()
    }

    fun list(): List<FileMapping> {
        TODO()
    }

    fun get(path: Path): ByteArray {
        TODO()
    }

    fun diff(old: Version, new: Version): List<FilesChanged> {
        val moved: List<FilesChanged> = old.idPairs.map { oldPair ->
            new.idPairs.filter { it.hash == oldPair.hash && it.path != oldPair.path }
                .map { FilesChanged.Moved(oldPair.path, it.path) }
        }.flatten()
        val deleted = old.idPairs.filter { oldPair -> new.idPairs.none { it.hash == oldPair.hash } }
            .map { FilesChanged.Removed(it.path) }
        val added = new.idPairs.filter { newPair -> old.idPairs.none { it.hash == newPair.hash } }.map {
            FilesChanged.Added(it.path)
        }
        val changed = new.idPairs.filter { newPair ->
            old.idPairs.any { it.hash != newPair.hash && it.path == newPair.path }
        }.map { FilesChanged.Changed(it.path) }
        return moved + deleted + added + changed
    }
}