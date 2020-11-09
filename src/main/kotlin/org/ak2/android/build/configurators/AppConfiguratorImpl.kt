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

import com.android.build.gradle.AppExtension
import org.ak2.android.build.AppConfigurator
import org.ak2.android.build.BaseAppConfigurator.AppDebugConfigurator
import org.ak2.android.build.BaseAppConfigurator.AppReleaseConfigurator
import org.ak2.android.build.DependenciesConfigurator.DependencyBuilder
import org.ak2.android.build.NativeConfigurator.NativeOptionsBuilder
import org.ak2.android.build.ResourceCheckConfigurator.StringCheckOptions
import org.ak2.android.build.flavors.VariantConfig
import org.ak2.android.build.manifests.applyAdditionalManifests
import org.ak2.android.build.tasks.HardCleanTask.Companion.addHardCleanTask
import org.gradle.api.Project
import org.gradle.api.file.FileCollection

class AppConfiguratorImpl(project: Project) : BaseAndroidConfiguratorKt(project, "com.android.application"),
    AppConfigurator {

    private val appFlavor = AppFlavorConfiguratorImpl(this, project.name).apply { enabled = true }

    private val lowLevelHooks = LowLevelConfiguratorImpl<AppExtension>();

    override var additionalManifests : FileCollection? = null

    override val languages: MutableSet<String>
        get() = appFlavor.languages

    override val densities: MutableSet<String>
        get() = appFlavor.languages

    override fun version(block: AppVersionKt.() -> Unit) = appFlavor.version(block)
    override fun dependsOn(block: DependencyBuilder.() -> Unit) = knownDependencies.block()
    override fun release(block: AppReleaseConfigurator.() -> Unit) = appFlavor.release(block)
    override fun debug(block: AppDebugConfigurator.() -> Unit) = appFlavor.debug(block)
    override fun nativeOptions(block: NativeOptionsBuilder.() -> Unit) = appFlavor.nativeOptions(block)
    override fun checkStrings(block: StringCheckOptions.() -> Unit) = appFlavor.checkStrings(block)

    fun configure(block: AppConfiguratorImpl.() -> Unit) = configureProject { this.block() }

    override fun buildVariants(variantConfigs: LinkedHashMap<String, VariantConfig>) {
        appFlavor.buildVariants(variantConfigs)
    }

    override fun configureFlavors() {
        super.configureFlavors();
        appFlavor.configure(project.androidExtension, appFlavor.config)
    }

    override fun before(block: AppExtension.() -> Unit) {
        lowLevelHooks.before(block);
    }

    override fun after(block: AppExtension.() -> Unit) {
        lowLevelHooks.after(block);
    }

    override fun beforeConfiguration() {
        lowLevelHooks.beforeConfiguration(project.androidExtension as AppExtension)

        project.addHardCleanTask()
    }

    override fun afterConfiguration() {
        lowLevelHooks.afterConfiguration(project.androidExtension as AppExtension)
    }

    override fun configureManifests() {
        project.applyAdditionalManifests(additionalManifests)
    }
}