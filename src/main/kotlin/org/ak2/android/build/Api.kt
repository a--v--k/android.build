package org.ak2.android.build

import org.ak2.android.build.configurators.*
import org.ak2.android.build.dependencies.base.*
import org.ak2.android.build.flavors.AndroidPlatforms
import org.ak2.android.build.flavors.NativeAbiType
import org.ak2.android.build.flavors.NativePlatforms
import org.ak2.android.build.flavors.native32
import org.ak2.android.build.release.ReleaseCallback
import org.gradle.api.initialization.Settings
import org.gradle.kotlin.dsl.KotlinBuildScript

fun KotlinBuildScript.androidLibrary(block : LibraryConfigurator.() -> Unit) {
    LibraryConfiguratorImpl(this).configure(block)
}

fun KotlinBuildScript.androidApp(block : AppConfigurator.() -> Unit) {
    AppConfiguratorImpl(this).configure(block)
}

fun KotlinBuildScript.androidAppSet(block : AppSetConfigurator.() -> Unit) {
    AppSetConfiguratorImpl(this).configure(block)
}

fun Settings.global(block : GlobalSettingsConfigurator.() -> Unit) {
    GlobalSettingsConfiguratorImpl(this).configure(block)
}

interface GlobalSettingsConfigurator {

    fun module(path: String)
    fun module(alias: String, path: String)
}

interface LibraryConfigurator : BuildConfigurator, DependenciesConfigurator, NativeConfigurator {

    val config : ProjectConfiguration
}

interface BaseAppConfigurator: DependenciesConfigurator, NativeConfigurator {

    val config : ProjectConfiguration

    fun version(block: AppVersionKt.() -> Unit)

    fun release(block : AppReleaseConfigurator.()-> Unit)
    fun debug(block: AppDebugConfigurator.() -> Unit)

    interface AppReleaseConfigurator : BuildConfigurator {

        fun onRelease(releaseCallback: ReleaseCallback)
    }

    interface AppDebugConfigurator : BuildConfigurator {
    }
}

interface AppConfigurator : BaseAppConfigurator

interface AppSetConfigurator : DependenciesConfigurator, NativeConfigurator {

    fun app(appName : String, block: AppFlavorConfigurator.() -> Unit)

    interface AppFlavorConfigurator : BaseAppConfigurator {

        fun id(id: String)
    }
}

interface BuildConfigurator {

    fun buildFor(flavor: AndroidPlatforms, innerBuilder: VariantBuilder.() -> Unit = {})

    interface VariantBuilder {
        fun native(flavor: NativePlatforms)
        fun native(flavors: List<NativePlatforms>)

        fun native32() = NativePlatforms.values().filter { it.abiType == NativeAbiType._32 }
        fun native64() = NativePlatforms.values().filter { it.abiType == NativeAbiType._64 }
        fun actual(platforms: List<NativePlatforms>) = platforms.filter { !it.depricated }
    }
}

interface DependenciesConfigurator {

    fun dependsOn(block: DependencyBuilder.() -> Unit)

    interface DependencyBuilder {

        val api             : ScopedDependencies
        val implementation  : ScopedDependencies
        val compileOnly     : ScopedDependencies
        val test            : ScopedDependencies
        val testRuntime     : ScopedDependencies

        fun module(alias: String)           : ModuleDependencyKt
        fun modules(vararg aliases: String) : List<ModuleDependencyKt>
        fun library(dependency: String)     : LibraryDependencyKt
        fun local(localPath: String)        : LocalJarDependencyKt
    }

    interface ScopedDependencies {

        val scope: DependencyScope

        operator fun plusAssign(dep: DependencyKt)

        operator fun plusAssign(deps: List<DependencyKt>)
    }
}

interface NativeConfigurator {

    fun nativeOptions(block: NativeOptionsBuilder.() -> Unit)

    interface NativeOptionsBuilder {

        fun   libraries(vararg args : String)
        fun executables(vararg args : String)

        fun        args(vararg args  : String)
        fun     c_flags(vararg flags : String)
        fun   cpp_flags(vararg flags : String)
    }
}

