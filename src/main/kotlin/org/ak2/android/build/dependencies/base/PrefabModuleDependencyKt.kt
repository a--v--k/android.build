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

package org.ak2.android.build.dependencies.base

import com.android.build.gradle.BaseExtension
import org.ak2.android.build.configurators.addVariantConfigurator
import org.ak2.android.build.configurators.androidProject
import org.ak2.android.build.configurators.getVariantConfigs
import org.ak2.android.build.extras.doOnce
import org.ak2.android.build.flavors.VariantNameBuilder
import org.ak2.android.build.utils.findByNameAndConfigure
import org.gradle.api.GradleException
import org.gradle.api.Task

class PrefabModuleDependencyKt(val dependencyPath: String) : DependencyKt {

    override fun configure(appName: String?, scope: DependencyScope, android: BaseExtension) {
        val project = android.androidProject

        doOnce(android, "prefabFeature") {
            android.buildFeatures.prefab = true
            true
        }

        android.addVariantConfigurator { variant ->
            val variantConfigs = android.getVariantConfigs();
            val variantConfig = variantConfigs[variant.name]
            require(variantConfig != null) { GradleException("Variant config missed for ${variant.name}: ${variantConfigs.keys}") }

            if (appName == null || variant.name.startsWith(appName)) {
                println("${project.path}: Add module dependency $dependencyPath[${scope.scope}]")

                val ownTask = "generateJsonModel${variant.name.capitalize()}"

                val expectedPrefabVariantName = VariantNameBuilder()
                    .append(variantConfig.androidFlavor)
                    .append(variantConfig.nativeFlavor)
                    .append(variantConfig.buildType.id)
                    .build()

                val prefabTask = "${dependencyPath}:prefab${expectedPrefabVariantName.capitalize()}Package"

                project.run {
                    dependencies.add(scope.scope, project(dependencyPath))

                    tasks.findByNameAndConfigure<Task>(ownTask) {
                        dependsOn += prefabTask
                    }
                }
            } else {
                println("${project.path}/${variant.name}: Skip module dependency $dependencyPath")
            }
        }
    }
}