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

import org.ak2.android.build.AppReleaseInfo
import org.ak2.android.build.utils.findByNameAndConfigure
import org.gradle.api.Project
import org.gradle.api.tasks.bundling.Zip
import java.util.*
import java.util.stream.Collectors

fun Project.createZip(appInfo: AppReleaseInfo, archiveName: String, destDir: String, zipConfigurator: Zip.() -> Unit) {

    val task = DistributionTasks.customZipDistributionTask(this, appInfo, archiveName)

    task.init(appInfo, archiveName, destDir, zipConfigurator)
}

fun Project.createI18nArchives(appInfo: AppReleaseInfo, languagesToPack: Set<String>? = null) {
    val appName = appInfo.name

    getLocales(appName, languagesToPack).forEach { locale ->
        createZip(appInfo, "i18n-$locale", "i18n") {
            getLocaleFolders(appName, locale).forEach { folder ->
                from(folder) {
                    this.include("strings_*.xml")
                }
            }
        }
    }
}

fun Project.getLocales(appName: String, languagesToPack: Set<String>? = null): Set<String> {
    val sourceSets = setOf("main", appName)

    return project.fileTree("src").apply { include("*/res/values*/strings_*.xml") }.files.stream()
        .map { it.parentFile }
        .distinct()
        .filter { sourceSets.contains(it.parentFile.parentFile.name) }
        .map { getLocale(it.name) }
        .filter { it != null }
        .filter { languagesToPack?.contains(it) ?: true }
        .collect(Collectors.toCollection { TreeSet() })
}

fun Project.getLocaleFolders(appName: String, locale: String): List<String> {
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
        if (candidate.isNotBlank() && Locale.forLanguageTag(candidate) != null) {
            return candidate
        }
    }
    return null
}

