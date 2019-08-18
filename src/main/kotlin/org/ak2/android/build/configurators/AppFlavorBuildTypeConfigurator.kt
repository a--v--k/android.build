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

package org.ak2.android.build.configurators

import org.ak2.android.build.BaseAppConfigurator.AppDebugConfigurator
import org.ak2.android.build.BaseAppConfigurator.AppReleaseConfigurator
import org.ak2.android.build.BuildConfigurator
import org.ak2.android.build.flavors.FlavorConfigs
import org.ak2.android.build.flavors.VariantConfig
import org.ak2.android.build.release.ReleaseCallback
import org.ak2.android.build.release.addReleaseCallback

sealed class AppFlavorBuildTypeConfigurator(val buildType: String, val appFlavor: AppFlavorConfiguratorImpl, val localFlavors : FlavorConfigs = FlavorConfigs()) : BuildConfigurator by localFlavors {

    fun buildVariants(variantConfigs: LinkedHashMap<String, VariantConfig>) {
        if (localFlavors.isEmpty()) {
            val buildTypeConfig = VariantConfig(buildType = buildType)
            variantConfigs.put(buildTypeConfig.name.value, buildTypeConfig)
        } else {
            localFlavors.toFlavors()
                .map {
                    if (appFlavor.singleAppMode) {
                        it.toVariantConfig(this.buildType)
                    } else {
                        it.toVariantConfig(this.buildType, appFlavor)
                    }
                }
                .forEach {
                    variantConfigs.put(it.name.value, it)
                }
        }
    }

    class AppReleaseConfiguratorImpl(appFlavor: AppFlavorConfiguratorImpl) : AppFlavorBuildTypeConfigurator("release", appFlavor), AppReleaseConfigurator {

        override fun onRelease(releaseCallback: ReleaseCallback) {
            appFlavor.parent.project.addReleaseCallback(appFlavor.name, releaseCallback)
        }
    }

    class AppDebugConfiguratorImpl(appFlavor: AppFlavorConfiguratorImpl) : AppFlavorBuildTypeConfigurator("debug", appFlavor), AppDebugConfigurator
}