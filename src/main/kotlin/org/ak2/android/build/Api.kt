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

package org.ak2.android.build

import com.android.build.gradle.AppExtension
import com.android.build.gradle.LibraryExtension
import org.ak2.android.build.buildtype.BuildTypeId
import org.ak2.android.build.configurators.AppVersion
import org.ak2.android.build.configurators.ProjectConfiguration
import org.ak2.android.build.dependencies.base.*
import org.ak2.android.build.flavors.AndroidPlatforms
import org.ak2.android.build.flavors.NativeAbiType
import org.ak2.android.build.flavors.NativePlatforms
import org.ak2.android.build.ndk.NdkOptions
import org.ak2.android.build.signing.ProguardConfig
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.file.FileCollection

interface LowLevelLibraryConfigurator {

    fun before(block : LibraryExtension.() -> Unit);

    fun after(block : LibraryExtension.() -> Unit);
}

interface LowLevelAppConfigurator {

    fun before(block : AppExtension.() -> Unit);

    fun after(block : AppExtension.() -> Unit);
}

interface RootConfigurator : RepositoryConfigurator {

    val project : Project

    val config : ProjectConfiguration
}

interface AndroidStudioConfigurator {
    var defaultBuildType        : BuildTypeId?
    var defaultAndroidPlatform  : AndroidPlatforms?
    var defaultNativePlatform   : NativePlatforms?
}

interface LibraryConfigurator : BuildConfigurator, RepositoryConfigurator, DependenciesConfigurator, NativeConfigurator, LowLevelLibraryConfigurator {

    val project : Project

    val config : ProjectConfiguration
}

interface AppReleaseInfo {
    val name : String
    val packageName : String
    val version : AppVersion
}

typealias ReleaseCallback = (appInfo: AppReleaseInfo) -> Unit

interface BaseAppConfigurator: DependenciesConfigurator, NativeConfigurator {

    val project : Project

    val config : ProjectConfiguration

    val languages : MutableSet<String>

    val densities : MutableSet<String>

    fun version(block: AppVersion.() -> Unit)

    fun release(block : AppReleaseConfigurator.()-> Unit)

    fun debug(block: AppDebugConfigurator.() -> Unit)

    interface AppReleaseConfigurator : BuildConfigurator {

        fun onRelease(releaseCallback: ReleaseCallback)

        fun proguard(block: ProguardConfig.() -> Unit)
    }

    interface AppDebugConfigurator : BuildConfigurator {
    }
}

interface AppConfigurator : BaseAppConfigurator, RepositoryConfigurator, ResourceCheckConfigurator, ManifestConfigurator, LowLevelAppConfigurator

interface AppSetConfigurator : RepositoryConfigurator, DependenciesConfigurator, NativeConfigurator, ManifestConfigurator, LowLevelAppConfigurator {

    fun app(appName : String, id : String? = null, enabled: Boolean? = true, block: AppFlavorConfigurator.() -> Unit)

    interface AppFlavorConfigurator : BaseAppConfigurator, ResourceCheckConfigurator {

        val name : String

        val packageName : String

        var id : String?

        var enabled : Boolean

        var default : Boolean
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

        fun module(path: String)            : ModuleDependencyKt
        fun modules(vararg aliases: String) : List<ModuleDependencyKt>
        fun library(dependency: String)     : LibraryDependencyKt
        fun local(localPath: String)        : LocalJarDependencyKt

        fun prefab(path: String)           : PrefabModuleDependencyKt
        fun prefabs(vararg aliases: String) : List<PrefabModuleDependencyKt>
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

        fun prefab(name: String, headersDir: String?)

        fun libraries(vararg args: String)
        fun executables(vararg args: String)

        fun args(vararg args: String)
        fun c_flags(vararg flags: String)
        fun cpp_flags(vararg flags: String)

        fun apply(ndk : NdkOptions)
    }
}

interface ResourceCheckConfigurator {

    fun checkStrings(block : StringCheckOptions.() -> Unit)

    interface StringCheckOptions {

        val languagesToCheck : MutableSet<String>
    }
}

interface ManifestConfigurator {

    var additionalManifests : FileCollection?
}

interface RepositoryConfigurator {

    fun addRepositories(block: RepositoryHandler.() -> Unit)
}

