package org.ak2.android.build.i18n

import org.ak2.android.build.utils.*
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.nio.file.Path
import javax.xml.parsers.DocumentBuilderFactory

data class Resources(
        var strings: List<ResourceString> = ArrayList()
)

data class ResourceString(
        var name: String? = null,
        var translatable: Boolean = true,
        var value: String? = null
)

fun readStringResources(path: Path): Resources {
    val doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(path.toFile())
    val strings = doc.documentElement.getElementsByTagName("string").toNodeList().asSequence()
            .filter { it is Element }
            .map { it as Element }
            .map {
                ResourceString(
                        name = it.getAttribute("name"),
                        translatable = "true" == it.getAttribute("translatable").orDefault("true"),
                        value = it.textContent
                )
            }.toList()

    return Resources(strings = strings)
}

fun NodeList.toNodeList(): List<Node> {
    val result = ArrayList<Node>(this.length)
    for (i in 0 until this.length) {
        result.add(this.item(i))
    }
    return result
}

