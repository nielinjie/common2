package xyz.nietongxue.common.base



import io.github.encryptorcode.pluralize.Pluralize


fun String.pluralize(count: Int = 5): String {
    return Pluralize.pluralize(this, count)
}

fun String.singular(): String {
    return Pluralize.pluralize(this, 1)
}

fun String.md5(): String {
    return java.security.MessageDigest.getInstance("MD5")
        .digest(this.toByteArray())
        .map { String.format("%02x", it) }
        .joinToString("")
}