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

package org.ak2.android.build.ndk

import com.android.build.api.variant.VariantProperties
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.internal.dsl.ProductFlavor
import org.ak2.android.build.NativeConfigurator
import org.ak2.android.build.configurators.addPostConfigurator
import org.ak2.android.build.configurators.androidProject
import org.ak2.android.build.configurators.getVariantConfigs
import org.ak2.android.build.extras.doOnce
import org.ak2.android.build.extras.putExtraIfAbsent
import org.gradle.api.GradleException
import org.gradle.api.tasks.Copy

const val JNI_ANDROID_MK = "src/main/jni/Android.mk"

class NativeConfiguratorImpl : NativeOptions(), NativeConfigurator.NativeOptionsBuilder {

    data class PrefabLibrary(val name: String, val headersDir: String?)

    private val prefabLibraries = mutableListOf<PrefabLibrary>()
    private val targets = mutableListOf<String>()
    private val executablePlugins = mutableListOf<String>()

    override fun prefab(name: String, headersDir: String?) {
        prefabLibraries += PrefabLibrary(name, headersDir)
        // targets += name
    }

    override fun libraries(vararg args: String) {
        targets += args
    }

    override fun executables(vararg args: String) {
        targets += args; executablePlugins += args
    }

    fun configure(android: BaseExtension, flavor: ProductFlavor? = null) {
        val ndkBuildRequired =
            putExtraIfAbsent(android, "setupNdkBuild") { android.setupNdkBuild() }
        if (!ndkBuildRequired) {
            return
        }

        if (prefabLibraries.isNotEmpty()) {
            require(android is LibraryExtension) { throw GradleException("Prefab publishing available only in Android Library modules")}

            android.run {
                buildFeatures {
                    prefabPublishing = true
                }

                prefabLibraries.forEach { lib ->
                    prefab.register(lib.name) {
                        if (!lib.headersDir.isNullOrEmpty()) {
                            this.headers = androidProject.file(lib.headersDir).absolutePath
                        }
                    }
                }
            }
        }

        val ndkBuildOptions = resolveNdkBuildOptions(android, flavor)
        if (ndkBuildOptions != null) {
            println("${android.androidProject.path}: Confgure native options for ${flavor?.getName() ?: android.androidProject.name}:")
            println("${android.androidProject.path}:      args=${args}")
            println("${android.androidProject.path}:   c_flags=${c_flags}")
            println("${android.androidProject.path}: cpp_flags=${cpp_flags}")

            super.configure(ndkBuildOptions)

            ndkBuildOptions.targets.clear()
            ndkBuildOptions.targets.addAll(targets)

            if (executablePlugins.isNotEmpty()) {
                doOnce(android, "NativePluginsConfiguration") {
                    android.addPostConfigurator {
                        addPluginTasks(android, it)
                    }
                }
            }
        }

    }

    private fun addPluginTasks(android: BaseExtension, variantProperties: VariantProperties) {
        val variantName = variantProperties.name
        val variants = android.getVariantConfigs()
        val variantConfig = variants[variantName]

        require(variantConfig != null) { GradleException("Add native build tasks: variant config missed for ${variantName}: ${variants.keys}") }

        val abiFlavor = variantConfig.nativeFlavor

        if (abiFlavor == null) {
            println("${android.androidProject.path}: cannot find NativeAbiFlavors for ${variantName}")
            return
        }

        val arch = abiFlavor.abi

        val capitalizedVariantName = variantName.capitalize()
        val buildTaskName = "externalNativeBuild$capitalizedVariantName"
        val moveTaskName = "movePlugins$capitalizedVariantName"
        val packageTaskName = "package$capitalizedVariantName"
        val mergeNativeLibsTaskName = "merge${capitalizedVariantName}NativeLibs"

        // externalNativeBuildEbookdroid-ngAndroid41xArm7Debug
        android.androidProject.run {

            val packageTask = tasks.findByName(packageTaskName)
            if (packageTask != null) {
                packageTask.dependsOn.add(moveTaskName)
            } else {
                tasks.whenTaskAdded {
                    if (name == packageTaskName) {
                        dependsOn.add(moveTaskName)
                    }
                }
            }

            tasks.register(moveTaskName, Copy::class.java) {
                dependsOn += buildTaskName
                dependsOn += mergeNativeLibsTaskName

                val fromDir =
                    android.androidProject.file("build/intermediates/ndkBuild/${variantName}/obj/local/$arch")
                val toDir =
                    android.androidProject.file("build/intermediates/merged_native_libs/${variantName}/out/lib/$arch")
                from(fromDir) {
                    include(executablePlugins)
                }
                into(toDir)
                rename("(.+)", "lib$1.so")
            }
        }
    }

    private fun resolveNdkBuildOptions(android: BaseExtension, flavor: ProductFlavor? = null) =
        flavor?.externalNativeBuild?.externalNativeNdkBuildOptions
            ?: android.defaultConfig.externalNativeBuild.externalNativeNdkBuildOptions
}

fun BaseExtension.setupNdkBuild(): Boolean {
    val androidMkFile = androidProject.file(JNI_ANDROID_MK)
    if (!androidMkFile.exists()) {
        return false
    }

    println("${androidProject.path}: Configure native build...")

    defaultConfig {
        externalNativeBuild {
            ndkBuild {
                abiFilters.clear()
            }
        }
    }

    buildTypes.maybeCreate("release").externalNativeBuild {
        ndkBuild {
            this.cFlags += "-DANDROID_APP_RELEASE"
        }
    }

    externalNativeBuild {
        ndkBuild {
            path = androidMkFile
        }
    }

    return true
}