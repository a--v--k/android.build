package org.ak2.android.build.extras

import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.plugins.ExtraPropertiesExtension

fun extras(model: Any?): ExtraPropertiesExtension {
    return if (model is ExtensionAware) model.extensions.extraProperties else throw java.lang.IllegalArgumentException("Bad model class ${model?.javaClass}")
}

inline fun doOnce(model: Any?, actionName: String, action: () -> Any) {
    val ext = extras(model)
    if (!ext.has(actionName)) {
        val value = action()
        ext.set(actionName, value)
    }
}

inline fun <reified T> getExtra(model: Any?, name: String): T? {
    val ext = extras(model)
    return if (ext.has(name)) ext.get(name) as T? else null
}

inline fun <reified T> getExtraOrDefault(model: Any?, name: String, defValue: T): T {
    val ext = extras(model)
    return if (ext.has(name)) ext.get(name) as T else defValue
}

inline fun <reified T> setExtra(model: Any?, name: String, value: T) {
    extras(model).set(name, value)
}

inline fun <reified T> putExtraIfAbsent(model: Any?, name: String, supplier: () -> T): T {
    val ext = extras(model)
    if (ext.has(name)) {
        return ext.get(name) as T
    }
    val value = supplier()
    ext.set(name, value)
    return value
}

inline fun <reified T> Project.getProjectExtra(name: String, defValue: T): T {
    var current: Project? = this
    while (current != null) {
        val extras = extras(current)
        val value = if (extras.has(name)) extras.get(name) else null
        if (value is T) {
            return value
        }
        current = current.parent
    }
    return defValue
}

