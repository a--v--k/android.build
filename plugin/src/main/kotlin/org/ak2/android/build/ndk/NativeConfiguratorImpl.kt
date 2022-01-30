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

import com.android.build.api.variant.Variant
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.internal.dsl.ProductFlavor
import com.android.build.gradle.internal.tasks.factory.dependsOn
import org.ak2.android.build.NativeConfigurator
import org.ak2.android.build.buildtype.BuildTypeId
import org.ak2.android.build.configurators.*
import org.ak2.android.build.extras.doOnce
import org.ak2.android.build.extras.putExtraIfAbsent
import org.ak2.android.build.flavors.VariantNameBuilder
import org.ak2.android.build.utils.findByNameAndConfigure
import org.gradle.api.GradleException
import org.gradle.api.Task
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

    override fun apply(ndk: NdkOptions) {
        args(
            ndk.common.appType,
            ndk.common.debug,
            ndk.cpp.stlLib
        )

        args(
            ndk.common.linkerFlags
        )

        c_flags(
            ndk.optLevel
        )

        cpp_flags(
            ndk.cpp.cppStandard,
            ndk.cpp.rtti,
            ndk.cpp.exceptions
        )

        apply(ndk.other)
    }

    fun configure(android: BaseExtension, flavor: ProductFlavor? = null) {
        val ndkBuildRequired =
            putExtraIfAbsent(android, "setupNdkBuild") { android.setupNdkBuild() }
        if (!ndkBuildRequired) {
            return
        }

        if (prefabLibraries.isNotEmpty()) {
            require(android is LibraryExtension) { throw GradleException("Prefab publishing available only in Android Library modules") }

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

                addVariantConfigurator { variant ->
                    val variantConfigs = android.getVariantConfigs()
                    val variantConfig = variantConfigs[variant.name]
                    require(variantConfig != null) { GradleException("Variant config missed for ${variant.name}: ${variantConfigs.keys}") }
                    require(variantConfig.nativeFlavor != null) { GradleException("Native ABI missed for ${variant.name}: ${variantConfigs.keys}") }

                    if (variantConfig.buildType == BuildTypeId.RELEASE) {
                        val appName = flavor?.name
                        if (appName == null || variant.name.startsWith(appName)) {
                            val expectedPrefabVariantName = VariantNameBuilder()
                                .append(variantConfig.androidFlavor)
                                .append(variantConfig.nativeFlavor)
                                .append(variantConfig.buildType.id)
                                .build()

                            with(androidProject) {

                                val fixTaskName = "prefab${expectedPrefabVariantName.capitalize()}PackageFix"
                                val taskToFix   = "bundle${expectedPrefabVariantName.capitalize()}LocalLintAar"

                                val fixTask = tasks.register(fixTaskName)

                                prefabLibraries.forEach { lib ->
                                    val fixLibTaskName = "${fixTaskName}[${lib.name}]"

                                    println("${path}/${variantConfig.name}: register package fix task ${fixLibTaskName} for ${taskToFix}")

                                    val fromDir  = file("build/intermediates/ndkBuild/${variant.name}/obj/local/${variantConfig.nativeFlavor.abi}")
                                    val toDir    = file("build/intermediates/prefab_package/${variant.name}/prefab/modules/${lib.name}/libs/android.${variantConfig.nativeFlavor.abi}")
                                    val fileName = "lib${lib.name}.a"

                                    val fixLibTask = tasks.register(fixLibTaskName, Copy::class.java) {
                                        from(fromDir) {
                                            include(fileName)
                                        }
                                        into(toDir)
                                    }

                                    fixLibTask.dependsOn(taskToFix)
                                    println("${project.path}/${variantConfig}:  Task ${project.path}:$fixLibTaskName will be executed after ${project.path}:$taskToFix")

                                    fixTask.dependsOn(fixLibTask)
                                    println("${project.path}/${variantConfig}:  Task ${project.path}:$fixTaskName will be executed after ${project.path}:$fixLibTaskName")
                                }
                            }
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

    private fun addPluginTasks(android: BaseExtension, variantProperties: Variant) {
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

        val packageTaskName = if (android is LibraryExtension) {
            "copy${capitalizedVariantName}JniLibsProjectOnly"
        } else {
            "package$capitalizedVariantName"
        }

        val mergeNativeLibsTaskName = "merge${capitalizedVariantName}NativeLibs"

        // externalNativeBuildEbookdroid-ngAndroid41xArm7Debug
        android.androidProject.run {

            tasks.findByNameAndConfigure<Task>(packageTaskName) {
                dependsOn += moveTaskName
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

    androidProject.config.ndkVersion?.let {
        println("${androidProject.path}: Configure NDK version: ${it.version}")
        ndkVersion = it.version
    }

    defaultConfig {
        externalNativeBuild {
            ndkBuild {
                abiFilters.clear()
            }
        }
    }

    BuildTypeId.RELEASE.configure(this) {
        externalNativeBuild {
            ndkBuild {
                this.cFlags += "-DANDROID_APP_RELEASE"
            }
        }
    }

    externalNativeBuild {
        ndkBuild {
            path = androidMkFile
        }
    }

    return true
}