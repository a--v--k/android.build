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

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.extension.AndroidComponentsExtension
import com.android.build.api.extension.ApplicationAndroidComponentsExtension
import com.android.build.api.variant.VariantFilter
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.internal.dsl.ProductFlavor
import org.ak2.android.build.RepositoryConfigurator
import org.ak2.android.build.dependencies.KnownDependencies
import org.ak2.android.build.dependencies.base.DependencyScope
import org.ak2.android.build.dependencies.base.standardScopes
import org.ak2.android.build.dependencies.constraints.dependencyConstraints
import org.ak2.android.build.flavors.VariantConfig
import org.ak2.android.build.flavors.getDimensions
import org.ak2.android.build.flavors.toFlavors
import org.ak2.android.build.ndk.NativeConfiguratorImpl
import org.gradle.api.GradleException
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.kotlin.dsl.exclude
import java.util.concurrent.atomic.AtomicBoolean

abstract class BaseAndroidConfiguratorKt(val project: Project, val androidPluginId: String) : RepositoryConfigurator {

    val config: ProjectConfiguration
        get() = project.config

    protected val knownDependencies = KnownDependencies()
    protected val configured = AtomicBoolean()
    protected val native = NativeConfiguratorImpl()

    override fun addRepositories(block: RepositoryHandler.() -> Unit) {
        config.repositories = block
    }

    protected fun configureProject(block: () -> Unit) {
        if (configured.compareAndSet(false, true)) {

            println("${project.path}: Prepare project configuration...")
            block()
            println("${project.path}: Apply project configuration...")

            project.run {
                plugins.apply(androidPluginId)

                configureRespositories();
                configureKotlin()

                project.extensions.configure<BaseExtension>("android") {
                    println("${project.path}: Configure Android...")

                    project.link(this)

                    beforeConfiguration();

                    configureBaseProperties()
                    configureNative()
                    configureDefaultBuildType()
                    configureVariants()
                    configureFlavors()
                    configureMultidex()
                    configureDependencyConstraints()
                    configureDependencies()
                    configureManifests()

                    afterConfiguration();
                }
            }

            println("${project.path}: Finish project configuration")
        }
    }

    protected open fun beforeConfiguration() {
    }

    protected open fun afterConfiguration() {
    }

    protected fun Project.configureRespositories() {
        config.repositories(repositories)
        if (config.dropSupportLibrary) {
            configurations.all {
                exclude("com.android.support")
            }
        }
    }

    protected fun Project.configureKotlin() {
        if (config.useKotlinInProd || config.useKotlinInTest) {
            println("${this.path}: Configure Kotlin ${config.kotlinVersion} ...")

            plugins.apply("kotlin-android")
            plugins.apply("kotlin-kapt")

            val stdlibVersion = when (config.javaVersion) {
                JavaVersion.VERSION_1_6 -> "kotlin-stdlib-jdk6"
                JavaVersion.VERSION_1_7 -> "kotlin-stdlib-jdk7"
                JavaVersion.VERSION_1_8 -> "kotlin-stdlib-jdk8"
                else -> "kotlin-stdlib-jdk8"
            }

            knownDependencies.run {
                val stdlib = library("org.jetbrains.kotlin:$stdlibVersion:${config.kotlinVersion}")

                if (config.useKotlinInProd) {
                    implementation += stdlib
                } else {
                    // If only Test code should use Kotlin...
                    test += stdlib
                    // Remove Kotlin libraries from Production code
                    val excluded = setOf(
                        "ApiDependenciesMetadata",
                        "ImplementationDependenciesMetadata",
                        "RuntimeOnlyDependenciesMetadata",
                        "CompileClasspath",
                        "RuntimeClasspath"
                    )
                    configurations.all {
                        val isProduction = excluded.any {
                            name.endsWith(it) && !name.endsWith("Test${it}")
                        }
                        if (isProduction) {
                            exclude(group = "org.jetbrains.kotlin")
                        }
                    }
                }
            }
        }
    }

    protected fun configureBaseProperties() {
        println("${project.path}: Configure base properties...")

        val minSdkVersion = config.minSdkVersion.code
        val compileSdkVersion = config.compileSdkVersion.code
        val targetSdkVersion = config.effectiveTargetSdkVersionCode
        val buildTools = config.buildToolsVersion
        val javaVersion = config.javaVersion

        project.androidExtension.apply {

            println("${project.path}: set compileSdkVersion to ${compileSdkVersion}")

            compileSdkVersion(compileSdkVersion)

            buildToolsVersion(buildTools)

            compileOptions {
                sourceCompatibility = javaVersion
                setTargetCompatibility(javaVersion)
            }

            buildFeatures.viewBinding = config.useViewBindings

            lintOptions {
                isAbortOnError = false
            }

            defaultConfig {
                minSdk = minSdkVersion
                targetSdk = targetSdkVersion
                javaCompileOptions {
                    annotationProcessorOptions {
                        arguments += hashMapOf("androidManifestFile" to "${project.projectDir}/src/main/AndroidManifest.xml")
                    }
                }
            }
        }
    }

    protected fun configureDefaultBuildType() {
        project.studioConfig.defaultBuildType?.configure(project.androidExtension) {
            isDefault = true
        }
    }

    protected fun configureVariants() {
        println("${project.path}: Configure variants...")
        val androidExtension = project.androidExtension

        val variantProcessor = androidExtension.getVariantProcessor()
        val variantConfigs = androidExtension.getVariantConfigs()

        buildVariants(variantConfigs)
        println("${project.path}: available variants: ${variantConfigs.keys}")

        androidExtension.addVariantValidator {
            checkApplicationFlavors(androidExtension, it)
        }

        androidExtension.variantFilter {
            variantProcessor.doFilter(this)
        }

        val androidComponents = project.extensions.getByName("androidComponents") as AndroidComponentsExtension<*, *,*>
        with(androidComponents) {
            onVariants(selector()) {
                variantProcessor.onVariantProperties(it)
            }
        }
    }

    protected fun checkApplicationFlavors(android: BaseExtension, variantFilter: VariantFilter): Boolean {
        if (variantFilter.flavors.isEmpty()) {
            return true
        }

        val variantConfigs = android.getVariantConfigs();
        val variant = variantConfigs[variantFilter.name]

        return variant?.enabled ?: false
    }

    protected abstract fun buildVariants(variantConfigs: LinkedHashMap<String, VariantConfig>);

    protected open fun configureFlavors() {
        println("${project.path}: Configure flavors...")

        project.androidExtension.addVariantConfigurator { variant ->
            val dependencyName = getDependencyName(variant.name)
            project.configurations.maybeCreate(dependencyName)
            println("${project.path}: Add configuration $dependencyName for this project")
        }

        val variantConfigs = project.androidExtension.getVariantConfigs()
        val dimensionsNames = variantConfigs.values.getDimensions()
        println("${project.path}: Add flavors $dimensionsNames for this project")

        project.androidExtension.flavorDimensions(* dimensionsNames.toTypedArray())

        variantConfigs.values.toFlavors().forEach { it.configure(project.androidExtension, config) }
    }

    protected fun configureMultidex() {
        val multidexFile = project.file("multidex.cfg")
        if (multidexFile.isFile) {
            println("${project.path}: Configure multidex APK...")

            project.androidExtension.defaultConfig {
                multiDexEnabled = true
                multiDexKeepProguard = multidexFile
            }

            knownDependencies.implementation += knownDependencies.library("androidx.multidex:multidex:2.0.1")
        }
    }

    protected fun configureDependencyConstraints() {
        println("${project.path}: Configure dependency constraints...")
        for (scope in DependencyScope.standardScopes) {
            project.configurations.maybeCreate(scope.scope)
            val constraints = project.dependencyConstraints
            for (constraint in constraints.values) {
                constraint.apply(project.dependencies.constraints, scope)
            }
        }
    }

    protected fun configureDependencies(appName: String? = null) {
        println("${project.path}: Configure dependencies...")
        knownDependencies.configure(appName, project.androidExtension)
    }

    protected fun configureNative(flavor: ProductFlavor? = null) {
        println("${project.path}: Configure native properties...")
        native.configure(project.androidExtension, flavor)
    }

    open protected fun configureManifests() {
    }
}
