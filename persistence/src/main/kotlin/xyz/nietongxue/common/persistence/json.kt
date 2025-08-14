package xyz.nietongxue.common.persistence


import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import xyz.nietongxue.common.base.Id
import xyz.nietongxue.common.base.IdGetter
import java.net.URI
import java.nio.file.Files
import java.util.concurrent.locks.ReentrantLock
import kotlin.io.path.Path
import kotlin.io.path.readText
import kotlin.io.path.writeText


interface Lock {
    fun lock()
    fun unlock()
}


open class ListPersistence<T>(val file: URI, val clazz: Class<T>) {
    val om = ObjectMapper()
    val list = mutableListOf<T>()
    private val lock = createLock()
    private fun save() {
        val jsonArray = om.createArrayNode().addAll(list.map { om.valueToTree<JsonNode>(it) })
        writeToPath(file, jsonArray.toString())
    }

    fun load() {
        readFromPath(file)?.let {
            val jsonArray = om.readTree(it) as? ArrayNode ?: error("no array find")
            jsonArray.map {
                om.treeToValue(it, clazz)
            }.let {
                list.addAll(it)
            }
        }
    }

    fun set(list: List<T>) {
        withList {
            it.clear()
            it.addAll(list)
        }
    }

    fun update(list: List<T>, idGetter: IdGetter<T>) {
        withList {
            list.forEach { item ->
                val index = it.indexOfFirst { idGetter(it) == idGetter(item) }
                if (index == -1) {
                    it.add(item)
                } else {
                    it[index] = item
                }
            }
        }
    }

    fun remove(item: T) {
        withList { it.remove(item) }
    }

    fun remove(item: T, idGetter: IdGetter<T>) {
        withList {
            val index = it.indexOfFirst { idGetter(it) == idGetter(item) }
            if (index != -1) {
                it.removeAt(index)
            }
        }
    }

    fun removeById(id: Id, idGetter: IdGetter<T>) {
        withList {
            val index = it.indexOfFirst { idGetter(it) == id }
            if (index != -1) {
                it.removeAt(index)
            }
        }
    }

    fun add(item: T) {
        withList { it.add(item) }
    }

    fun withList(block: (MutableList<T>) -> Unit) {
        lock.lock()
        block(list)
        save()
        lock.unlock()
    }

    fun withMutableMap(idGetter: IdGetter<T>, block: (MutableMap<Id, T>) -> Unit) {
        lock.lock()
        val map = list.associateBy { idGetter(it)!! }.toMutableMap()
        block(map)
        val newList = map.values.toList()
        list.clear()
        list.addAll(newList)
        save()
        lock.unlock()
    }

}


fun writeToPath(path: URI, content: String) {
    val file = Path(path.path)
    Files.createDirectories(file.parent)
    file.writeText(content)
}

fun readFromPath(path: URI): String? {
    val file = (Path(path.path))
    if (!Files.exists(file)) {
        return null
    }
    return file.readText()
}


fun createLock(): Lock {
    return object : Lock {
        val l = ReentrantLock()
        override fun lock() {
            l.lock()
        }

        override fun unlock() {
            l.unlock()
        }
    }
}