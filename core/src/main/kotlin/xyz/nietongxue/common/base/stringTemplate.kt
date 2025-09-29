
package xyz.nietongxue.common.base



fun replacePlaceholders(
    template: String,
    values: Map<String, String>,
    namingFun: (String) -> String = { "$$it" }
): String {
    var result = template
    for (entry in values.entries) {
        result = result.replace(namingFun(entry.key), entry.value)
    }
    return result
}