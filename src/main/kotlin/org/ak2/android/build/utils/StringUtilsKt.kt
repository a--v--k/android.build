package org.ak2.android.build.utils

val xmlEntities = mapOf (
        "&" to "&amp;",
        "<" to "&lt;",
        "\"" to "&quot;",
        ">" to "&gt;",
        "\n" to "\\n"
)

fun String?.nullToEmpty() : String {
    return this ?: ""
}

fun String?.orDefault(def : String) : String {
    val str = this.nullToEmpty()
    return if (str.isEmpty()) def else str
}

fun String.normalize(): String {
    var result = this
    for ((symbol, entity) in xmlEntities.entries) {
        result = Regex.fromLiteral(symbol).replace(result, entity)
    }
    return result
}

fun cleanupString(str: String) = Regex.fromLiteral("\\n").replace(str, "")

