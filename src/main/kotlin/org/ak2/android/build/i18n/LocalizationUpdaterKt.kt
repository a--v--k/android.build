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

package org.ak2.android.build.i18n

import com.android.build.gradle.BaseExtension
import org.ak2.android.build.ResourceCheckConfigurator.StringCheckOptions
import org.ak2.android.build.utils.*
import org.apache.commons.io.IOUtils
import org.gradle.api.GradleException
import org.languagetool.JLanguageTool
import org.languagetool.Language
import org.languagetool.Languages
import org.languagetool.rules.RuleMatch
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.nio.file.Path
import java.nio.file.Paths

const val DEF_LOCALIZATION = "values"

fun checkLocalization(
    android: BaseExtension,
    appName: String? = null,
    options: StringCheckOptions
) {
    doTask(android, appName, options) { appLocalization -> checkAppLocalization(appLocalization) }
}

fun updateLocalization(
    android: BaseExtension,
    appName: String? = null,
    options: StringCheckOptions
) {
    doTask(android, appName, options) { appLocalization -> updateAppLocalization(appLocalization) }
}

fun doTask(android: BaseExtension, appName: String? = null, options : StringCheckOptions, task: (AppLocalization) -> Unit) {
    android.sourceSets.forEach { sourceSet ->
        val name = sourceSet.implementationConfigurationName
        if (name == "implementation" || appName != null && name.startsWith(appName)) {
            sourceSet.res.sourceDirectoryTrees.forEach { tree ->
                if (tree.dir.exists()) {
                    val appLocalization = loadAppLocalization(tree.dir.toPath(), options)
                    task.invoke(appLocalization)
                }
            }
        }
    }
}

fun checkAppLocalization(appLocalization: AppLocalization) {
    println("====\nChecking $appLocalization...")

    val defLocalization = appLocalization.localizations[Paths.get(DEF_LOCALIZATION)]
    if (defLocalization == null) {
        println("No default localization for $appLocalization")
        return
    }

    val defLang = defLocalization.getLanguage()
    val defLangTool = defLang?.let { JLanguageTool(it) }

    println("====\nChecking default strings...")

    defLocalization.files.values.forEach { defFile ->
        defFile.strings.values.forEach { defStr ->
            val stringToCheck = cleanupString(defStr.value)
            val matches = defLangTool?.check(stringToCheck) ?: emptyList<RuleMatch>()
            matches.forEach { match ->
                println("${defLocalization.relativeDirPath}/${defFile.relativeFilePath}/${defStr.name}: $stringToCheck")
                println("${defLocalization.relativeDirPath}/${defFile.relativeFilePath}/${defStr.name}: Potential error at characters ${match.fromPos}-${match.toPos}: ${match.message}")
                println("${defLocalization.relativeDirPath}/${defFile.relativeFilePath}/${defStr.name}: Suggested correction(s): ${match.suggestedReplacements}")
            }
        }
    }

    appLocalization.localizations.values.stream().filter { l -> l != defLocalization }.forEach { localization ->
        println("====\nChecking ${localization.lang}...")
        val lang = localization.getLanguage()
        val langTool = lang?.let { JLanguageTool(it) }

        defLocalization.files.values.forEach { defFile ->
            val file = localization.files[defFile.relativeFilePath]
            if (file != null) {
                defFile.strings.values.forEach { defStr ->
                    val str = file.strings[defStr.name]

                    when {
                        (str == null && defStr.translatable) -> {
                            println("${localization.relativeDirPath}/${file.relativeFilePath} has no string ${defStr.name}")
                        }

                        (str != null && !defStr.translatable) -> {
                            println("${localization.relativeDirPath}/${file.relativeFilePath} overrides non translatable string ${defStr.name}")
                        }

                        str != null && defStr.translatable && str.value == defStr.value -> {
                            println("${localization.relativeDirPath}/${file.relativeFilePath} has non translated string ${defStr.name}")
                        }

                        str != null && langTool != null -> {
                            val stringToCheck = cleanupString(str.value)
                            val matches = langTool.check(stringToCheck) ?: emptyList<RuleMatch>()
                            matches.forEach { match ->
                                println("${localization.relativeDirPath}/${file.relativeFilePath}/${defStr.name}: $stringToCheck")
                                println("${localization.relativeDirPath}/${file.relativeFilePath}/${defStr.name}: Potential error at characters ${match.fromPos}-${match.toPos}: ${match.message}")
                                println("${localization.relativeDirPath}/${file.relativeFilePath}/${defStr.name}: Suggested correction(s): ${match.suggestedReplacements}")
                            }
                        }
                    }
                }
            } else {
                println("${localization.relativeDirPath} has no ${defFile.relativeFilePath}")
            }
        }
    }
}

fun updateAppLocalization(appLocalization: AppLocalization) {
    val defLocalization = appLocalization.localizations[Paths.get(DEF_LOCALIZATION)]
    if (defLocalization == null) {
        println("No default localization for $appLocalization")
        return
    }

    val defFiles = defLocalization.files.keys

    appLocalization.localizations.values.stream().filter { l -> l != defLocalization }.forEach { localization ->
        defLocalization.files.values.forEach { defFile ->
            val strings = localization.strings

            try {
                val outputFile = getFile(appLocalization, localization, defFile)
                val output = BufferedWriter(FileWriter(outputFile))

                try {
                    output.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>")
                    output.write("\n")
                    output.write("<resources xmlns:xliff=\"urn:oasis:names:tc:xliff:document:1.2\">")
                    output.write("\n")
                    output.write("\n")

                    defFile.strings.values.forEach { defStr ->
                        if (defStr.translatable) {
                            val str = strings[defStr.name]
                            if (str != null) {
                                output.write("    <string name=\"${str.name}\">\"${str.value.normalize()}\"</string>")
                            } else {
                                output.write("<!--<string name=\"${defStr.name}\">\"${defStr.value.normalize()}\"</string>-->")
                            }
                            output.write("\n")
                        }
                    }

                    output.write("\n")
                    output.write("</resources>")
                    output.write("\n")

                    println("Written $outputFile")
                } finally {
                    IOUtils.closeQuietly(output)
                }

            } catch (ex: IOException) {
                throw GradleException("Unexpected error", ex)
            }
        }

        localization.files.values.forEach { localFile ->
            if (!defFiles.contains(localFile.relativeFilePath)) {
                val outputFile = getFile(appLocalization, localization, localFile)
                outputFile.delete()
                println("Removed $outputFile")
            }
        }
    }
}

fun getFile(appLocalization: AppLocalization, localizationDir: LocalizationDir, localizationFile: LocalizationFile): File {
    return appLocalization.absolutePath.resolve(localizationDir.relativeDirPath).resolve(localizationFile.relativeFilePath).toFile()
}

fun loadAppLocalization(path: Path, options: StringCheckOptions): AppLocalization {
    val result = AppLocalization(absolutePath = path)

    path.listDirectories { p -> isLocalizationDir(p, options) }.forEach { absValuesPath ->
        val dir = loadLocalizationDir(absValuesPath, options)
        result.localizations[dir.relativeDirPath] = dir
    }

    return result
}

fun loadLocalizationDir(absValuesPath: Path, options: StringCheckOptions): LocalizationDir {
    val result = LocalizationDir(relativeDirPath = absValuesPath.getLastSegment(), lang = getLang(absValuesPath, options))

    absValuesPath.listFiles { f -> f.getLastName() == "_strings.xml" }.forEach(loadLocalizationFile(result))

    if (result.files.isEmpty()) {
        absValuesPath.listFiles { f -> f.getLastName().startsWith("strings") }.forEach(loadLocalizationFile(result))
    } else {
        println("Localization ${result.relativeDirPath} contains import localization file, ignore others")
    }

    return result
}

fun loadLocalizationFile(result: LocalizationDir): (Path) -> Unit {
    return { absFilePath ->
        val file = loadLocalizationFile(absFilePath)
        result.files[file.relativeFilePath] = file

        file.strings.values.forEach { str ->
            val prev = result.strings.put(str.name, str)
            if (prev != null) {
                println("Localization ${result.relativeDirPath}/${file.relativeFilePath} overrides string ${prev.name} from ${prev.parent}")
            }
        }
    }
}


fun loadLocalizationFile(absFilePath: Path): LocalizationFile {
    val file = LocalizationFile(relativeFilePath = absFilePath.getLastSegment())

    val resources = readStringResources(absFilePath)

    resources.strings.forEach { str ->
        val name = str.name.nullToEmpty()
        var text = str.value.nullToEmpty()
        val translatable = str.translatable

        if (text.startsWith("\"") && text.endsWith("\"")) {
            text = text.substring(1, text.length - 1)
        }

        val localizedString = LocalizedString(parent = file, name = name, value = text, translatable = translatable)

        file.strings[localizedString.name] = localizedString
    }

    return file
}

fun isLocalizationDir(
    path: Path,
    options: StringCheckOptions
): Boolean {
    val name = path.getLastName()
    if (name == DEF_LOCALIZATION) {
        return true
    }
    if (name.startsWith("values-")) {
        val locale = name.substring("values-".length)
        if (options.languagesToCheck.contains(locale)) {
            return true
        }
    }
    return false
}

fun getLang(path: Path, options: StringCheckOptions): String? {
    val name = path.getLastName()
    if (name == DEF_LOCALIZATION) {
        return "en"
    }
    if (name.startsWith("values-")) {
        val locale = name.substring("values-".length)
        if (options.languagesToCheck.contains(locale)) {
            return locale
        }
    }
    return null
}

data class LocalizedString(
        val parent: LocalizationFile,
        val name: String,
        val value: String,
        val translatable: Boolean
) {
    override fun toString() :String {
        return "$name[$translatable]=$value"
    }
}

data class LocalizationFile(
        val relativeFilePath: Path,
        val strings: MutableMap<String, LocalizedString> = LinkedHashMap()
)

data class LocalizationDir(
        val relativeDirPath: Path,
        val lang: String?,
        val files: MutableMap<Path, LocalizationFile> = LinkedHashMap(),
        val strings: MutableMap<String, LocalizedString> = LinkedHashMap()
) {
    fun getLanguage(): Language? {
        try {
            if (lang != null) {
                return Languages.getLanguageForShortCode(lang)
            }
        } catch (ex: IllegalArgumentException) {

        }
        return null
    }
}

data class AppLocalization(
        val absolutePath: Path,
        val localizations: MutableMap<Path, LocalizationDir> = LinkedHashMap()
) {
    override fun toString() : String {
        return "$absolutePath: ${localizations.keys}"
    }
}

