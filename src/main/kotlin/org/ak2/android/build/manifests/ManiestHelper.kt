package org.ak2.android.build.manifests

import com.android.build.gradle.tasks.ProcessApplicationManifest
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.internal.provider.AbstractProperty
import org.gradle.api.internal.provider.DefaultListProperty
import java.io.File
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

fun Project.applyAdditionalManifests(additionalManifests: FileCollection?) {

    val manifests = additionalManifests?.files ?: emptySet()
    if (manifests.isEmpty()) {
        logger.info("${this.path}: No additional manifests")
        return;
    }

    afterEvaluate {
        tasks
            .filter { it is ProcessApplicationManifest }
            .map { it as ProcessApplicationManifest }
            .forEach { it.applyAdditionalManifests(manifests) }
    }
}


fun ProcessApplicationManifest.applyAdditionalManifests(additionalManifests : Set<File>) {

    val stateField = AbstractProperty::class.memberProperties.find { it.name == "state" }
    val state = stateField?.getPropertyValueByReflection(manifestOverlays as DefaultListProperty<File>)
    requireNotNull(state)

    val disallowChangesField = state.javaClass.getDeclaredField("disallowChanges")
    disallowChangesField?.setFieldValueByReflection(state, false)

    manifestOverlays.addAll(additionalManifests.sorted())

    val msg = (manifestOverlays.orNull ?: emptyList()).joinToString("\n\t", "\n\t")
    println("${project.path} manifest overlays: ${msg}")
}

inline fun <reified T, reified R> KProperty1<T, R>.getPropertyValueByReflection(receiver: T): R {
    isAccessible = true
    return get(receiver)
}

inline fun <reified T, reified R> java.lang.reflect.Field.setFieldValueByReflection(receiver: T, value: R) {
    isAccessible = true
    return set(receiver, value)
}
