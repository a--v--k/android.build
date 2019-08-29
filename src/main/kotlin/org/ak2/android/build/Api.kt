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

import org.ak2.android.build.configurators.AppVersionKt
import org.ak2.android.build.configurators.ProjectConfiguration
import org.ak2.android.build.dependencies.base.*
import org.ak2.android.build.flavors.AndroidPlatforms
import org.ak2.android.build.flavors.NativeAbiType
import org.ak2.android.build.flavors.NativePlatforms
import org.ak2.android.build.release.ReleaseCallback
import org.ak2.android.build.signing.ProguardConfig

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

        fun proguard(block: ProguardConfig.() -> Unit)
    }

    interface AppDebugConfigurator : BuildConfigurator {
    }
}

interface AppConfigurator : BaseAppConfigurator, ResourceCheckConfigurator

interface AppSetConfigurator : DependenciesConfigurator, NativeConfigurator {

    fun app(appName : String, id : String? = null, block: AppFlavorConfigurator.() -> Unit)

    interface AppFlavorConfigurator : BaseAppConfigurator, ResourceCheckConfigurator {

        val name : String

        var id : String?

        var enabled : Boolean
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

        fun module(path: String)           : ModuleDependencyKt
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

interface ResourceCheckConfigurator {

    fun checkStrings(block : StringCheckOptions.() -> Unit)

    interface StringCheckOptions {

        val languagesToCheck : MutableSet<String>
    }
}
