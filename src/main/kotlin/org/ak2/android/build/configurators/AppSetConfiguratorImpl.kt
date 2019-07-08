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

import org.ak2.android.build.AppSetConfigurator
import org.ak2.android.build.AppSetConfigurator.AppFlavorConfigurator
import org.ak2.android.build.DependenciesConfigurator.DependencyBuilder
import org.ak2.android.build.NativeConfigurator.NativeOptionsBuilder
import org.ak2.android.build.flavors.VariantConfig
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.kotlin.dsl.KotlinBuildScript

class AppSetConfiguratorImpl(project: KotlinBuildScript) : BaseAndroidConfiguratorKt(project, "com.configurators.application"), AppSetConfigurator {

    private val knownApplications = project.applicationFolders()
            .map { AppFlavorConfiguratorImpl(this, it.name, it) }
            .onEach { println("${project.path}: application flavor found: ${it.name}") }
            .associateTo(LinkedHashMap()) { it.name to it }

    override fun app(appName: String, block: AppFlavorConfigurator.() -> Unit) {
        require(knownApplications.containsKey(appName)) { GradleException("Unknown application name ${project.path}/$appName") }
        knownApplications[appName]?.configure(block)
    }

    override fun dependsOn(block: DependencyBuilder.() -> Unit) {
        knownDependencies.block()
    }

    override fun nativeOptions(block: NativeOptionsBuilder.() -> Unit) {
        native.block()
    }

    fun configure(block: AppSetConfiguratorImpl.() -> Unit) = configureProject { this.block() }

    override fun buildVariants(variantConfigs: LinkedHashMap<String, VariantConfig>) {
        knownApplications.values.forEach { it.buildVariants(variantConfigs) }
    }
}

fun Project.applicationFolders() = this.fileTree("src").apply { include("*/AndroidManifest.xml") }.files.asSequence()
        .map { it.parentFile }
        .filter { it.name != "main" }
