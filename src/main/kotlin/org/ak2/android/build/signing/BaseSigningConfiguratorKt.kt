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

package org.ak2.android.build.signing

import com.android.build.gradle.BaseExtension
import org.ak2.android.build.buildtype.BuildTypeId
import org.ak2.android.build.configurators.androidProject
import org.ak2.android.build.extras.doOnce
import java.io.File

open class BaseSigningConfiguratorKt(val buildType: BuildTypeId, val configName: String = "${buildType}SigningConfig", var params: SigningConfigParams? = null) {

    fun defineSigningConfig(android: BaseExtension) {
        defineSigningConfig(android) { resolvedKeystoreFile, params ->
            buildType.configure(android) {
                signingConfig = signingConfigs.maybeCreate(configName).apply {
                    storeFile = resolvedKeystoreFile
                    storePassword = params.storePassword
                    keyAlias = params.keyAlias
                    keyPassword = params.keyPassword
                    println("${android.androidProject.path}: signingConfig created - $configName:$resolvedKeystoreFile")
                }
            }
        }
    }

    protected fun defineSigningConfig(android: BaseExtension, block: BaseExtension.(File, SigningConfigParams) -> Unit) {
        val actualParams = params
        if (actualParams == null) {
            println("${android.androidProject.path}: keystore file not defined for $buildType")
            return
        }

        val fileName = actualParams.storeFile
        val file = android.androidProject.rootProject.file(fileName)
        require(file.exists()) { "Keystore file not found: $fileName" }

        doOnce(android, configName) {
            android.block(file, actualParams)
        }
    }
}