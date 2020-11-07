/*
 * Copyright 2019 AK2.
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

package org.ak2.android.build.release

import org.ak2.android.build.configurators.androidExtension
import org.ak2.android.build.configurators.getVariantNames
import org.gradle.api.Project
import java.util.*
import java.util.stream.Collectors

import org.ak2.android.build.extras.*
import org.ak2.android.build.utils.findByNameAndConfigure
import org.gradle.api.GradleException
import org.gradle.api.Task
import org.gradle.api.tasks.bundling.Zip

typealias ReleaseCallback = (appName: String, appVersion: String) -> Unit

val knownLocales : Set<String> = Arrays.stream(Locale.getAvailableLocales()).map(Locale::toString).collect(Collectors.toSet())

fun Project.addReleaseCallback(appName: String, callback: ReleaseCallback) {
    getReleaseCallbacks(project, appName).add(callback)
}

fun getReleaseCallbacks(project: Project, appName: String) = putExtraIfAbsent(project, "releaseCallbacks$appName") { ArrayList<ReleaseCallback>() }

fun createZip(project: Project, appName: String, archName: String, appVersion: String, destDir: String, zipConfigurator: Zip.() -> Unit) {
    val zipTaskName = "zip${appName.capitalize()}${archName.capitalize()}"
    val path = "build/outputs/packages/release/$appName/$appVersion/$destDir"

    project.run {
        val zipTask = tasks.create(zipTaskName, Zip::class.java)

        zipTask.group = "ak2"
        zipTask.archiveBaseName.set(appName)
        zipTask.archiveAppendix.set(archName)
        zipTask.archiveVersion.set(appVersion)
        zipTask.destinationDirectory.set(file(path))

        zipTask.zipConfigurator()

        project.androidExtension.getVariantNames().stream().filter { it.startsWith(appName) && it.endsWith("Release") }.forEach {
            val buildTaskName = "assemble${it.capitalize()}"
            tasks.findByNameAndConfigure<Task>(buildTaskName) {
                dependsOn += zipTask
                println("${project.path}: add $zipTask to $this")
            }
        }
    }
}

fun createI18nArchives(project: Project, appName: String, appVersion: String, languagesToPack : Set<String>? = null) {
    getLocales(project, appName, languagesToPack).forEach { locale ->
        createZip(project, appName, "i18n-$locale", appVersion, "i18n") {
            getLocaleFolders(project, appName, locale).forEach { folder ->
                from(folder) {
                    this.include("strings_*.xml")
                }
            }
        }
    }
}

fun getLocales(project: Project, appName: String, languagesToPack : Set<String>? = null): Set<String> {
    val sourceSets = setOf("main", appName)

    return project.fileTree("src").apply { include("*/res/values*/strings_*.xml") }.files.stream()
            .map { it.parentFile }
            .distinct()
            .filter { sourceSets.contains(it.parentFile.parentFile.name) }
            .map { getLocale(it.name) }
            .filter { it != null }
            .filter { languagesToPack?.contains(it) ?: true}
            .collect(Collectors.toCollection { TreeSet() })
}

fun getLocaleFolders(project: Project, appName: String, locale: String): List<String> {
    val sourceSets = setOf("main", appName)
    val folders = ArrayList<String>()

    (if (locale == "en") listOf("values", "values-en") else listOf("values-$locale")).forEach { folder ->
        sourceSets.forEach { sourceSetName ->
            val path = "src/$sourceSetName/res/$folder"
            val file = project.file(path)
            if (file.isDirectory) {
                folders.add(path)
            }
        }
    }
    return folders
}

fun getLocale(folderName: String): String? {
    if (folderName == "values") {
        return "en"
    }
    if (folderName.startsWith("values-")) {
        val candidate = folderName.substring("values-".length).split("-")[0]
        if (candidate.isNotBlank() && Locale.forLanguageTag(candidate)!= null) {
            return candidate
        }
    }
    return null
}

