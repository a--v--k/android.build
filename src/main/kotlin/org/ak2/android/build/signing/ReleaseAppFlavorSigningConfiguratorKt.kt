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
import com.android.build.gradle.internal.dsl.ProductFlavor
import org.ak2.android.build.buildtype.BuildTypeId
import org.ak2.android.build.configurators.androidProject

class ReleaseAppFlavorSigningConfiguratorKt(appName: String, params: SigningConfigParams? = null) : BaseSigningConfiguratorKt(BuildTypeId.RELEASE, "${appName}ReleaseSigningConfig", params) {

    fun defineSigningConfig(android: BaseExtension, appFlavor: ProductFlavor) {
        defineSigningConfig(android) { resolvedKeystoreFile, params ->
            appFlavor.signingConfig = signingConfigs.maybeCreate(configName).apply {
                storeFile = resolvedKeystoreFile
                storePassword = params.storePassword
                keyAlias = params.keyAlias
                keyPassword = params.keyPassword
                println("${android.androidProject.path}: signingConfig created - $configName:$resolvedKeystoreFile")
            }
        }
    }
}
