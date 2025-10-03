package xyz.nietongxue.common.string



import io.github.encryptorcode.pluralize.Pluralize


fun String.pluralize(count: Int = 5): String {
    return Pluralize.pluralize(this, count)
}

fun String.singular(): String {
    return Pluralize.pluralize(this, 1)
}

