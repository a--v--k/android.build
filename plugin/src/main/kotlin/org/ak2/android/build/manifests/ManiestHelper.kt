/*
 * Copyright 2020 AK2.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
