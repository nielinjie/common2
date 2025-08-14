package xyz.nietongxue.common.persistence

import jakarta.annotation.PostConstruct
import xyz.nietongxue.common.base.Path
import java.nio.file.Path as FPath


open class PersistentList<T>(file: FPath, clazz: Class<T>) :
    ListPersistence<T>(file.toUri(), clazz) {
    @PostConstruct
    fun onLoad() {
        load()
    }
}


class FileTreePersistentList(val mapping: PathMapToFileTree) {
    private val persistentMap = mutableMapOf<Path, PersistentList<*>>()


    fun <T> getOrCreate(path: Path, clazz: Class<T>): PersistentList<T> {
        @Suppress("UNCHECKED_CAST")
        return persistentMap.getOrPut(path) {
            PersistentList(mapping.getFPath(path), clazz).also {
                it.load()
            }
        } as PersistentList<T>

    }

    fun mappingPath(renterName: String) = Path(listOf(renterName))


}



