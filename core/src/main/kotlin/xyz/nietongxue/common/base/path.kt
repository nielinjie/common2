package xyz.nietongxue.common.base


data class Path(val parts: List<Name>) {
    constructor(vararg parts: Name) : this(parts.toList())

    companion object {
        val zero = Path(emptyList())
        fun fromString(path: String) = Path(path.split("/").filter { it.isNotBlank() })
    }

    fun append(part: Name) = this.append(
        fromString(part)
    )
    fun drop(n: Int=1) = Path(parts.drop(n))

    fun append(path: Path) = Path(this.parts + path.parts.filter { it.isNotBlank() })
    fun asString() = parts.joinToString("/")
    fun isChildOf(b: Path): Boolean {
        return (this.parts.size == b.parts.size + 1) && this.parts.subList(0, b.parts.size) == b.parts
    }

    fun isDescendantOf(b: Path): Boolean {
        return (this.parts.size > b.parts.size) && this.parts.subList(0, b.parts.size) == b.parts
    }

    fun parent(): Path? = if (this.parts.isNotEmpty()) Path(this.parts.subList(0, this.parts.size - 1)) else null
    fun ancestors(): List<Path> {
        return parts.indices.map { Path(parts.subList(0, it)) }
    }

    fun shortName(): String = parts.last()
    fun packageAndName() = PackageAndName(this)

    infix operator fun div (part: Name) = append(part)
    infix operator fun div (part: Path) = append(part)
    fun startsWith(prefix: Path): Boolean {
        return parts.size >= prefix.parts.size && parts.subList(0, prefix.parts.size) == prefix.parts
    }
    fun startsWith(string:String)  = startsWith(fromString(string))
    fun endsWith(suffix: Path): Boolean {
        return parts.size >= suffix.parts.size && parts.subList(parts.size - suffix.parts.size, parts.size) == suffix.parts
    }
    fun endsWith(string:String)  = endsWith(fromString(string))
}

class PackageAndName(val path: Path) {
    val name = path.shortName()
    val packageName = path.parent()
}
