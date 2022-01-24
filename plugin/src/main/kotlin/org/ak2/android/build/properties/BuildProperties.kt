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

package org.ak2.android.build.properties

import com.google.common.base.Charsets
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.util.*

class BuildProperties(val appFolder: File, parent: BuildProperties? = null) : PropertyDelegateImpl(parent?.properties ?: System.getProperties()) {

    val buildPropertiesExist: Boolean
    val localPropertiesExist: Boolean

    init {
        val buildProperties = File(appFolder, "build.properties")
        val localProperties = File(appFolder, "local.properties")

        buildPropertiesExist = buildProperties.isFile
        localPropertiesExist = localProperties.isFile

        listOf(buildProperties, localProperties)
            .filter { file -> file.isFile }
            .map { file -> InputStreamReader(FileInputStream(file), Charsets.UTF_8) }
            .forEach { reader -> reader.use { properties.load(it) } }
    }

    override fun toString() = "BuildProperties[$appFolder, $buildPropertiesExist, $localPropertiesExist, ${properties.keys}]"
}