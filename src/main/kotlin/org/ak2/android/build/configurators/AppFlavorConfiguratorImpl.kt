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

import com.android.build.gradle.BaseExtension
import com.android.build.gradle.api.ApkVariant
import com.android.build.gradle.api.ApkVariantOutput
import com.android.build.gradle.internal.dsl.ProductFlavor
import org.ak2.android.build.AppSetConfigurator.AppFlavorConfigurator
import org.ak2.android.build.BaseAppConfigurator.AppDebugConfigurator
import org.ak2.android.build.BaseAppConfigurator.AppReleaseConfigurator
import org.ak2.android.build.DependenciesConfigurator.DependencyBuilder
import org.ak2.android.build.NativeConfigurator.NativeOptionsBuilder
import org.ak2.android.build.ResourceCheckConfigurator.StringCheckOptions
import org.ak2.android.build.dependencies.KnownDependencies
import org.ak2.android.build.extras.doOnce
import org.ak2.android.build.flavors.Flavor
import org.ak2.android.build.flavors.VariantConfig
import org.ak2.android.build.i18n.StringCheckOptionsImpl
import org.ak2.android.build.i18n.checkLocalization
import org.ak2.android.build.i18n.updateLocalization
import org.ak2.android.build.ndk.NativeConfiguratorImpl
import org.ak2.android.build.release.getReleaseCallbacks
import org.ak2.android.build.signing.DebugSigningConfiguratorKt
import org.ak2.android.build.signing.ReleaseAppFlavorSigningConfiguratorKt
import org.ak2.android.build.signing.ReleaseSigningConfiguratorKt
import org.gradle.api.GradleException
import java.io.File
import java.io.FileInputStream
import java.util.*
import kotlin.collections.LinkedHashSet


class AppFlavorConfiguratorImpl(
    val parent: BaseAndroidConfiguratorKt,
    override val name: String,
    appFolder: File = parent.project.projectDir
) : Flavor, AppFlavorConfigurator {

    val singleAppMode = appFolder == parent.project.projectDir

    override var id: String? = null

    override var enabled : Boolean = false

    override val dimensionName = "App"

    override val config = if (singleAppMode) parent.config else InnerProjectConfiguration(parent.project, appFolder, parent.config)

    override val languages : MutableSet<String> = LinkedHashSet()

    override val densities : MutableSet<String> = LinkedHashSet()

    private val _appVersion = AppVersionKt()

    private var _appProperties: Properties
    private var _proguardFile: File

    private val _localDependencies = KnownDependencies()
    private val _localNativeConfigurator = NativeConfiguratorImpl()
    private val _stringCheckOptions = StringCheckOptionsImpl()

    private val _releaseConfigurator = AppFlavorBuildTypeConfigurator.AppReleaseConfiguratorImpl(this)
    private val _debugConfigurator = AppFlavorBuildTypeConfigurator.AppDebugConfiguratorImpl(this)

    init {
        println("${parent.project.path}: App ${name}:${appFolder} init...")

        val buildProperties = File(appFolder, "build.properties")
        require(buildProperties.exists()) { "Application properties missed: ${buildProperties.absolutePath}" }

        _appProperties = loadFromFile(buildProperties)

        _localNativeConfigurator.load(parent.project, _appProperties, config.releaseSigningConfig)
        _appVersion.fromProperties(_appProperties)

        _proguardFile = File(appFolder, "proguard.cfg")
    }

    override fun version(block: AppVersionKt.() -> Unit)                = _appVersion.block()
    override fun dependsOn(block: DependencyBuilder.() -> Unit)         = _localDependencies.block()
    override fun release(block: AppReleaseConfigurator.() -> Unit)      = _releaseConfigurator.block()
    override fun debug(block: AppDebugConfigurator.() -> Unit)          = _debugConfigurator.block()
    override fun nativeOptions(block: NativeOptionsBuilder.() -> Unit)  = _localNativeConfigurator.block()
    override fun checkStrings(block: StringCheckOptions.() -> Unit)     = _stringCheckOptions.block()

    fun configure(block: AppFlavorConfiguratorImpl.() -> Unit) = apply {
        this.block()
    }

    fun buildVariants(variantConfigs: LinkedHashMap<String, VariantConfig>) {
        _releaseConfigurator.buildVariants(variantConfigs)
        _debugConfigurator.buildVariants(variantConfigs)
    }

    override fun configure(android: BaseExtension) {
        println("${android.androidProject.path}: Configure ${this.name}")

        val productFlavor: ProductFlavor? = singleAppMode.takeIf { !it }?.let {
            android.productFlavors.maybeCreate(this.name).apply {
                dimension = dimensionName
                applicationId = id
            }
        }

        _localNativeConfigurator.configure(android, productFlavor)

        if (!singleAppMode) {
            _localDependencies.configure(this.name, android)
        }

        if (_proguardFile.exists()) {
            println("${android.androidProject.path}: Configure proguard: ${_proguardFile}")
            productFlavor?.setProguardFiles(listOf(_proguardFile))

            doOnce(android, "ProguardConfig") {
                android.buildTypes.maybeCreate("release").apply {

                    setMinifyEnabled(config.proguardConfig.minifyEnabled)
                    setShrinkResources(config.proguardConfig.shrinkResources)

                    if (singleAppMode) {
                        setProguardFiles(listOf(_proguardFile))
                    }
                }
            }
        } else {
            println("${android.androidProject.path}: No proguard configuration available")
        }

        if (singleAppMode) {
            android.defaultConfig {
                if (languages.isNotEmpty()) {
                    resConfigs(languages)
                }
                if (densities.isNotEmpty()) {
                    resConfigs(densities)
                }
            }
        } else {
            productFlavor?.run {
                if (languages.isNotEmpty()) {
                    resConfigs(languages)
                }
                if (densities.isNotEmpty()) {
                    resConfigs(densities)
                }
            }
        }

        DebugSigningConfiguratorKt(config.debugSigningConfig).defineSigningConfig(android)

        if (productFlavor == null) {
            ReleaseSigningConfiguratorKt(config.releaseSigningConfig).defineSigningConfig(android)
        } else {
            ReleaseAppFlavorSigningConfiguratorKt(name, config.releaseSigningConfig).defineSigningConfig(android, productFlavor)
        }

        doOnce(android, "ApplicationVersionConfigurator") {
            android.addPostConfigurator {
                if (singleAppMode) {
                    updateSingleApplicationVersion(android, this, it as ApkVariant)
                } else {
                    updateMultiApplicationVersion(android, it as ApkVariant)
                }
            }
        }

        if (enabled) {
            android.addPostConfigurator {
                doOnce(android, "ApplicationReleaseConfigurator$name") {
                    getReleaseCallbacks(parent.project, name).forEach { it(name, _appVersion.versionName.orEmpty()) }
                }
            }
        }

        if (_stringCheckOptions.languagesToCheck.isNotEmpty()) {
            val appName = this.name
            android.androidProject.run {
                val organizeTaskName = "${appName}OrganizeStrings"
                val checkTaskName = "${appName}CheckStrings"

                println("${path}: Configure string check tasks: ${organizeTaskName} ${checkTaskName}")

                val organizeTask = tasks.maybeCreate(organizeTaskName)
                organizeTask.doLast { updateLocalization(android, appName, _stringCheckOptions) }
                organizeTask.group = "ak2"

                val checkTask = tasks.maybeCreate(checkTaskName)
                checkTask.doLast { checkLocalization(android, appName, _stringCheckOptions) }
                checkTask.group = "ak2"
            }
        }
    }

    override fun canUseAsDependency(that: Flavor): Boolean {
        return this == that
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return other is AppFlavorConfiguratorImpl && this.name == other.name
    }

    override fun toString(): String {
        return name
    }

    companion object {

        fun updateMultiApplicationVersion(android: BaseExtension, v: ApkVariant) {
            val variantConfigs = android.getVariantConfigs();
            val variant = variantConfigs[v.name]
            require(variant != null) { GradleException("Update app version: variant config missed for ${v.name}: ${variantConfigs.keys}") }

            val appFlavor = variant.appFlavor
            require(appFlavor != null) { "Application flavor missed for ${v.name}" }

            updateApplicationVersion(android, appFlavor, variant, v)
        }

        fun updateSingleApplicationVersion(android: BaseExtension, appFlavor: AppFlavorConfiguratorImpl, v: ApkVariant) {
            val variantConfigs = android.getVariantConfigs();
            val variant = variantConfigs[v.name]
            require(variant != null) { GradleException("Update app version: variant config missed for ${v.name}: ${variantConfigs.keys}") }

            updateApplicationVersion(android, appFlavor, variant, v)
        }

        fun updateApplicationVersion(android: BaseExtension, appFlavor: AppFlavorConfiguratorImpl, variant : VariantConfig, v: ApkVariant) {

            val appFlavorName = appFlavor.name
            val buildTypeName = variant.buildType

            val backToOutputs = if (variant.hasFlavors()) { "../../.." } else { "../.." }

            val apkFolder = "$backToOutputs/packages/$buildTypeName/$appFlavorName"

            val apkConfig = if (buildTypeName == "release") {
                ApkConfig.Release(android, appFlavor, variant, apkFolder)
            } else {
                ApkConfig.Debug(android, appFlavor, variant, apkFolder)
            }

            v.outputs.forEach {
                setAppProperties(v, it as ApkVariantOutput, apkConfig)
            }
        }

        private fun setAppProperties(v: ApkVariant, output: ApkVariantOutput, apkConfig : ApkConfig) {
            // TODO should be replaced later
            // val originFolder = output.outputFile.parentFile.canonicalFile
            // val target = File(originFolder, apkConfig.apkName).canonicalPath

            apkConfig.versionName?.takeIf { it.isNotEmpty() }?.let { versionName -> output.versionNameOverride = versionName }
            apkConfig.versionCode.takeIf  { it != 0         }?.let { versionCode -> output.versionCodeOverride = versionCode }

            output.outputFileName = apkConfig.apkPath
        }
    }

    sealed class ApkConfig(var apkPath: String? = null, var versionName : String? = null, var versionCode : Int = 0) {

        class Release(android: BaseExtension, appFlavor: AppFlavorConfiguratorImpl, variant: VariantConfig, apkFolder: String) : ApkConfig() {
            init {
                versionCode = appFlavor._appVersion.versionCode

                if (variant.hasFlavors()) {
                    val index = appFlavor._releaseConfigurator.localFlavors.toFlavors().indexOf(variant.toFlavorConfig());
                    require(index >= 0) { "Cannot find variant ${variant.name} in ${android.androidProject.path}" }
                    versionCode += index
                }

                versionName = appFlavor._appVersion.versionName

                val packageName = appFlavor._appProperties.getProperty("package.name", appFlavor.name)
                val apkName = listOf(packageName, versionName, versionCode.toString(), variant.suffix.value)
                    .filterNotNull()
                    .filter{ it.isNotEmpty() }
                    .joinToString(separator = "-")

                apkPath = "$apkFolder/${versionName}/${apkName}.apk"
            }
        }

        class Debug(android: BaseExtension, appFlavor: AppFlavorConfiguratorImpl, variant: VariantConfig, apkFolder: String) : ApkConfig() {
            init {
                val packageName = appFlavor._appProperties.getProperty("package.name", appFlavor.name)
                val apkName = listOf(packageName, variant.suffix.value, variant.buildType)
                    .filterNotNull()
                    .filter{ it.isNotEmpty() }
                    .joinToString(separator = "-")

                apkPath ="$apkFolder/$apkName.apk"

                android.androidProject.config.debugVersion?.let {
                    versionCode = it.versionCode
                    versionName = it.versionName
                }
            }
        }
    }
}

private fun loadFromFile(propertyFile: File): Properties {
    require(propertyFile.exists()) { "Properties file not found: ${propertyFile.absolutePath}" }

    val p = Properties()
    try {
        FileInputStream(propertyFile).use { fis -> p.load(fis) }
    } catch (ex: Exception) {
        ex.printStackTrace();
    }
    return p
}



