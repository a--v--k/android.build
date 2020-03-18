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

import com.android.build.gradle.BaseExtension
import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.internal.dsl.ProductFlavor
import org.ak2.android.build.NativeConfigurator
import org.ak2.android.build.extras.*
import org.ak2.android.build.configurators.*
import org.ak2.android.build.flavors.ANDROID_MK
import org.ak2.android.build.signing.SigningConfigParams
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import java.io.FileInputStream
import java.security.KeyStore
import java.security.cert.X509Certificate
import java.security.interfaces.RSAPublicKey
import java.util.*

const val executablePluginsAssetPath = "plugins/"

class NativeConfiguratorImpl : NativeOptions(), NativeConfigurator.NativeOptionsBuilder {

    private val targets = mutableListOf<String>()
    private val executablePlugins  = mutableListOf<String>()

    override fun   libraries(vararg args : String)  { targets += args  }
    override fun executables(vararg args : String)  { targets += args ; executablePlugins += args }

    fun configure(android: BaseExtension, flavor: ProductFlavor? = null) {
        val ndkBuildRequired = putExtraIfAbsent(android, "setupNdkBuild") { android.setupNdkBuild() }
        if (!ndkBuildRequired) {
            return
        }

        val ndkBuildOptions = resolveNdkBuildOptions(android, flavor)

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

    private fun addPluginTasks(android : BaseExtension, variant : BaseVariant) {
        val variants = android.getVariantConfigs()
        val variantConfig = variants[variant.name]
        require(variantConfig != null) { GradleException("Add native build tasks: variant config missed for ${variant.name}: ${variants.keys}")}

        val abiFlavor = variantConfig.nativeFlavor

        if (abiFlavor == null) {
            println("${android.androidProject.path}: cannot find NativeAbiFlavors for ${variant.name}")
            return
        }

        val arch = abiFlavor.abi

        val variantName = variant.name.capitalize()
        val buildTaskName = "externalNativeBuild$variantName"
        val moveTaskName = "movePlugins$variantName"
        val packageTaskName = "package$variantName"
        val mergeNativeLibsTaskName = "merge${variantName}NativeLibs"

        val iter = variant.productFlavors.iterator()
        var flavors = iter.next().name
        while (iter.hasNext()) {
            flavors += iter.next().name.capitalize()
        }

        android.androidProject.run {
            tasks.register(moveTaskName, Copy::class.java) {
                val fromDir = android.androidProject.file("build/intermediates/ndkBuild/${variant.name}/obj/local/$arch")
                val toDir = android.androidProject.file("build/intermediates/merged_native_libs/${variant.name}/out/lib/$arch")
                from(fromDir) {
                    include(executablePlugins)
                }
                into(toDir)
                rename("(.+)", "lib$1.so")
            }

            tasks.findByName(packageTaskName)?.dependsOn?.add(moveTaskName)
            tasks.findByName(moveTaskName)?.dependsOn?.apply {
                add(buildTaskName)
                add(mergeNativeLibsTaskName)
            }
        }
    }

    private fun resolveNdkBuildOptions(android: BaseExtension, flavor: ProductFlavor? = null) =
            flavor?.externalNativeBuild?.externalNativeNdkBuildOptions
            ?: android.defaultConfig.externalNativeBuild.externalNativeNdkBuildOptions
}

fun BaseExtension.setupNdkBuild() : Boolean {
    val androidMkFile = androidProject.file(ANDROID_MK)
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
            this.getcFlags().add("-DANDROID_APP_RELEASE")
        }
    }

    externalNativeBuild {
        ndkBuild {
            path = androidMkFile
        }
    }

    return true
}