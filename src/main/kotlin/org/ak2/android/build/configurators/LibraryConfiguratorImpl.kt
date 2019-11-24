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

import com.android.build.gradle.LibraryExtension
import org.ak2.android.build.BuildConfigurator.VariantBuilder
import org.ak2.android.build.DependenciesConfigurator.DependencyBuilder
import org.ak2.android.build.LibraryConfigurator
import org.ak2.android.build.NativeConfigurator.NativeOptionsBuilder
import org.ak2.android.build.flavors.AndroidPlatforms
import org.ak2.android.build.flavors.FlavorConfigs
import org.ak2.android.build.flavors.VariantConfig
import org.gradle.kotlin.dsl.KotlinBuildScript

class LibraryConfiguratorImpl(project: KotlinBuildScript) : BaseAndroidConfiguratorKt(project, "com.android.library"), LibraryConfigurator {

    private val flavors = FlavorConfigs()

    private val lowLevelHooks = LowLevelConfiguratorImpl<LibraryExtension>();

    override fun buildFor(flavor: AndroidPlatforms, innerBuilder: VariantBuilder.() -> Unit) { flavors.buildFor(flavor, innerBuilder) }

    override fun     dependsOn(block: DependencyBuilder.()    -> Unit) = knownDependencies.block()
    override fun nativeOptions(block: NativeOptionsBuilder.() -> Unit) = native.block()

    fun configure(block: LibraryConfiguratorImpl.() -> Unit) = configureProject { this.block() }

    override fun buildVariants(variantConfigs: LinkedHashMap<String, VariantConfig>) {
        val buildTypes = project.androidExtension.buildTypes.map { it.name }
        flavors.toFlavors()
                .flatMap { flavor -> buildTypes.asSequence().map { buildType ->
                    VariantConfig(buildType = buildType, androidFlavor = flavor.androidFlavor, nativeFlavor = flavor.nativeFlavor)
                    }
                }
                .forEach {
                    variantConfigs.put(it.name.value, it)
                }
    }

    override fun before(block: LibraryExtension.() -> Unit) {
        lowLevelHooks.before(block);
    }

    override fun after(block: LibraryExtension.() -> Unit) {
        lowLevelHooks.after(block);
    }

    override fun beforeConfiguration() {
        lowLevelHooks.beforeConfiguration(project.androidExtension as LibraryExtension)
    }

    override fun afterConfiguration() {
        lowLevelHooks.afterConfiguration(project.androidExtension as LibraryExtension)
    }
}