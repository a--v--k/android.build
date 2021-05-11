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

import com.android.build.api.variant.ApplicationVariant
import com.android.build.api.variant.VariantOutputConfiguration
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.internal.dsl.ProductFlavor
import org.ak2.android.build.AppSetConfigurator.AppFlavorConfigurator
import org.ak2.android.build.BaseAppConfigurator.AppDebugConfigurator
import org.ak2.android.build.BaseAppConfigurator.AppReleaseConfigurator
import org.ak2.android.build.DependenciesConfigurator.DependencyBuilder
import org.ak2.android.build.NativeConfigurator.NativeOptionsBuilder
import org.ak2.android.build.ResourceCheckConfigurator.StringCheckOptions
import org.ak2.android.build.buildtype.BuildTypeId
import org.ak2.android.build.dependencies.KnownDependencies
import org.ak2.android.build.extras.doOnce
import org.ak2.android.build.flavors.Flavor
import org.ak2.android.build.flavors.VariantConfig
import org.ak2.android.build.i18n.StringCheckOptionsImpl
import org.ak2.android.build.i18n.checkLocalization
import org.ak2.android.build.i18n.updateLocalization
import org.ak2.android.build.ndk.NativeConfiguratorImpl
import org.ak2.android.build.release.AppReleaseInfoImpl
import org.ak2.android.build.release.DistributionTasks
import org.ak2.android.build.signing.DebugSigningConfiguratorKt
import org.ak2.android.build.signing.ReleaseAppFlavorSigningConfiguratorKt
import org.ak2.android.build.signing.ReleaseSigningConfiguratorKt
import org.gradle.api.GradleException
import java.io.File
import java.util.*
import kotlin.collections.LinkedHashSet

class AppFlavorConfiguratorImpl(
    val parent: BaseAndroidConfiguratorKt,
    override val name: String,
    appFolder: File = parent.project.projectDir
) : Flavor, AppFlavorConfigurator {

    val singleAppMode = appFolder == parent.project.projectDir

    override val packageName : String
        get() = config.buildProperties.get("package.name", name)

    override var id: String? = null

    override var enabled: Boolean = false

    override var default: Boolean = false

    override val dimensionName = "App"

    override val project = parent.project

    override val config = if (singleAppMode) parent.config else InnerProjectConfiguration(parent.project, appFolder, parent.config)

    override val languages: MutableSet<String> = LinkedHashSet()

    override val densities: MutableSet<String> = LinkedHashSet()

    private val _appVersion = AppVersion()

    private var _proguardFile: File

    private val _localDependencies = KnownDependencies()
    private val _localNativeConfigurator = NativeConfiguratorImpl()
    private val _stringCheckOptions = StringCheckOptionsImpl()

    private val _releaseConfigurator = AppFlavorBuildTypeConfigurator.AppReleaseConfiguratorImpl(this)
    private val _debugConfigurator   = AppFlavorBuildTypeConfigurator.AppDebugConfiguratorImpl(this)

    init {
        println("${parent.project.path}: App ${name}:${appFolder} init...")

        require(config.buildProperties.buildPropertiesExist) { "Application properties missed: ${File(appFolder, "build.properties").absolutePath}" }

        println("${parent.project.path}: App build properties:\n${config.buildProperties}")

        _appVersion.fromProperties(config.buildProperties)

        _proguardFile = File(appFolder, "proguard.cfg")
    }

    override fun version(block: AppVersion.() -> Unit)                  = _appVersion.block()
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

    override fun configure(android: BaseExtension, projectConfig: ProjectConfiguration) {
        println("${android.androidProject.path}: Configure ${this.name}")

        val productFlavor: ProductFlavor? = singleAppMode.takeIf { !it }?.let {
            android.productFlavors.maybeCreate(this.name).apply {
                dimension = dimensionName
                applicationId = id

                if (default) {
                    isDefault = true
                }
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
                BuildTypeId.RELEASE.configure(android) {
                    isMinifyEnabled = config.proguardConfig.minifyEnabled
                    isShrinkResources = config.proguardConfig.shrinkResources

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
                    resourceConfigurations.addAll(languages)
                }
                if (densities.isNotEmpty()) {
                    resourceConfigurations.addAll(densities)
                }
            }
        } else {
            productFlavor?.run {
                if (languages.isNotEmpty()) {
                    resourceConfigurations.addAll(languages)
                }
                if (densities.isNotEmpty()) {
                    resourceConfigurations.addAll(densities)
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
                    updateSingleApplicationVersion(android,this, it as ApplicationVariant)
                } else {
                    updateMultiApplicationVersion(android, it as ApplicationVariant)
                }
            }
        }

        if (enabled) {
            doOnce(android, "ApplicationReleaseConfigurator$name") {
                _releaseConfigurator.releaseCallbacks.forEach {
                    it(AppReleaseInfoImpl(name, packageName, _appVersion))
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

        fun updateMultiApplicationVersion(android: BaseExtension, v: ApplicationVariant) {
            val variantConfigs = android.getVariantConfigs();
            val variant = variantConfigs[v.name]
            require(variant != null) { GradleException("Update app version: variant config missed for ${v.name}: ${variantConfigs.keys}") }

            val appFlavor = variant.appFlavor
            require(appFlavor != null) { "Application flavor missed for ${v.name}" }

            updateApplicationVersion(android, appFlavor, variant, v)
        }

        fun updateSingleApplicationVersion(android: BaseExtension, appFlavor: AppFlavorConfiguratorImpl, v: ApplicationVariant) {
            val variantConfigs = android.getVariantConfigs();
            val variant = variantConfigs[v.name]
            require(variant != null) { GradleException("Update app version: variant config missed for ${v.name}: ${variantConfigs.keys}") }

            updateApplicationVersion(android, appFlavor, variant, v)
        }

        fun updateApplicationVersion(android: BaseExtension, appFlavor: AppFlavorConfiguratorImpl, variant: VariantConfig, v: ApplicationVariant) {

            val apkConfig = when (variant.buildType) {
                BuildTypeId.RELEASE -> ApkConfig.Release(android, appFlavor, variant)
                BuildTypeId.DEBUG -> ApkConfig.Debug(android, appFlavor, variant)
                else -> throw GradleException("Custom build type not yet supported")
            }

            val mainOutput =
                v.outputs.single { it.outputType == VariantOutputConfiguration.OutputType.SINGLE }

            apkConfig.versionName?.takeIf { it.isNotEmpty() }?.let(mainOutput.versionName::set)
            apkConfig.versionCode.takeIf { it != 0 }?.let(mainOutput.versionCode::set)

            if (variant.buildType == BuildTypeId.RELEASE) {
                val rootDistTask = DistributionTasks.projectDistributionTask(android.androidProject)
                val appDistTask = DistributionTasks.appDistributionTask(android.androidProject, appFlavor)
                val apkDistTask = DistributionTasks.apkDistributionTask(android.androidProject, v)
                val proguardDistTask = DistributionTasks.proGuardMappingDistributionTask(android.androidProject, v)

                appDistTask.init(rootDistTask, appFlavor)
                apkDistTask.init(appDistTask, apkConfig, v)

                proguardDistTask.init(appDistTask, apkConfig, v)
                proguardDistTask.enabled = appFlavor._proguardFile.exists()
            }
        }
    }

    sealed class ApkConfig(var apkName: String? = null, var versionName: String? = null, var versionCode: Int = 0) {

        class Release(android: BaseExtension, appFlavor: AppFlavorConfiguratorImpl, variant: VariantConfig) : ApkConfig() {
            init {

                versionCode = appFlavor._appVersion.versionCode

                if (variant.hasFlavors()) {
                    if (!appFlavor._releaseConfigurator.localFlavors.isEmpty()) {
                        val index = appFlavor._releaseConfigurator.localFlavors.toFlavors().indexOf(variant.toFlavorConfig());
                        require(index >= 0) { "Cannot find variant ${variant.name} in ${android.androidProject.path}" }
                        versionCode += index
                    }
                }

                versionName = appFlavor._appVersion.versionName

                apkName = listOf(appFlavor.packageName, versionName, versionCode.toString(), variant.suffix.value)
                        .filterNotNull()
                        .filter { it.isNotEmpty() }
                        .joinToString(separator = "-")
            }
        }

        class Debug(android: BaseExtension, appFlavor: AppFlavorConfiguratorImpl, variant: VariantConfig) : ApkConfig() {
            init {
                apkName = listOf(appFlavor.packageName, variant.suffix.value, variant.buildType.id)
                    .filterNotNull()
                    .filter { it.isNotEmpty() }
                    .joinToString(separator = "-")

                android.androidProject.config.debugVersion?.let {
                    versionCode = it.versionCode
                    versionName = it.versionName
                }
            }
        }
    }
}
