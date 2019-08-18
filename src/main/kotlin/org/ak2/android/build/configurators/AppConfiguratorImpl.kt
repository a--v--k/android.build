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

import org.ak2.android.build.AppConfigurator
import org.ak2.android.build.BaseAppConfigurator.AppDebugConfigurator
import org.ak2.android.build.BaseAppConfigurator.AppReleaseConfigurator
import org.ak2.android.build.DependenciesConfigurator.DependencyBuilder
import org.ak2.android.build.NativeConfigurator.NativeOptionsBuilder
import org.ak2.android.build.ResourceCheckConfigurator.StringCheckOptions
import org.ak2.android.build.flavors.VariantConfig
import org.gradle.kotlin.dsl.KotlinBuildScript

class AppConfiguratorImpl(project: KotlinBuildScript) : BaseAndroidConfiguratorKt(project, "com.android.application"), AppConfigurator {

    private val appFlavor = AppFlavorConfiguratorImpl(this, getDefaultAppName(project.name)).apply { enabled = true }

    override fun       version(block: AppVersionKt.()           -> Unit) = appFlavor.version(block)
    override fun     dependsOn(block: DependencyBuilder.()      -> Unit) = knownDependencies.block()
    override fun       release(block: AppReleaseConfigurator.() -> Unit) = appFlavor.release(block)
    override fun         debug(block: AppDebugConfigurator.()   -> Unit) = appFlavor.debug(block)
    override fun nativeOptions(block: NativeOptionsBuilder.()   -> Unit) = appFlavor.nativeOptions(block)
    override fun checkStrings(block:  StringCheckOptions.()     -> Unit) = appFlavor.checkStrings(block)

    fun configure(block: AppConfiguratorImpl.() -> Unit)     = configureProject { this.block()  }

    override fun buildVariants(variantConfigs: LinkedHashMap<String, VariantConfig>) {
        appFlavor.buildVariants(variantConfigs)
    }

    override fun configureFlavors() {
        super.configureFlavors();
        appFlavor.configure(project.androidExtension)
    }

}