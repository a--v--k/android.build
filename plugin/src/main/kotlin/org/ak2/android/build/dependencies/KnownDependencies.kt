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

package org.ak2.android.build.dependencies

import com.android.build.gradle.BaseExtension
import org.ak2.android.build.DependenciesConfigurator.*
import org.ak2.android.build.dependencies.base.*
import java.util.*
import kotlin.collections.ArrayList

class KnownDependencies : DependencyBuilder {

    private val _dependencies = TreeMap<DependencyScope, ScopedDependenciesImpl>()

    private val _modules = TreeMap<String, ModuleDependencyKt>()
    private val _prefabs = TreeMap<String, PrefabModuleDependencyKt>()

    override val api                : ScopedDependencies get() = dependencies(DependencyScope.API)
    override val implementation     : ScopedDependencies get() = dependencies(DependencyScope.IMPLEMENTATION)
    override val compileOnly        : ScopedDependencies get() = dependencies(DependencyScope.COMPILE_ONLY)
    override val test               : ScopedDependencies get() = dependencies(DependencyScope.TEST)
    override val testRuntime        : ScopedDependencies get() = dependencies(DependencyScope.TEST_RUNTIME)
    override val androidTest        : ScopedDependencies get() = dependencies(DependencyScope.ANDROID_TEST)
    override val androidTestRuntime : ScopedDependencies get() = dependencies(DependencyScope.ANDROID_TEST_RUNTIME)

    private fun dependencies(scope : DependencyScope) = _dependencies.computeIfAbsent(scope, ::ScopedDependenciesImpl)

    override fun add(scope: Any, dependency: Any) {
        val actualScope = when (scope) {
            is DependencyScope  -> scope
            else                -> DependencyScope.of(scope.toString())
        }

        val actualDependency = when (dependency) {
            is DependencyKt     -> dependency
            else                -> DependencyWrapper(dependency)
        }

        dependencies(actualScope).plusAssign(actualDependency)
    }

    override fun module(path: String) = _modules.computeIfAbsent(path) { ModuleDependencyKt(dependencyPath = it) }

    override fun modules(vararg aliases: String) = aliases.map(this::module)

    override fun library(dependency: String) = LibraryDependencyKt(dependency)

    override fun local(localPath: String) = LocalJarDependencyKt(localPath)

    override fun prefab(path: String) = _prefabs.computeIfAbsent(path) { PrefabModuleDependencyKt(dependencyPath = it) }

    override fun prefabs(vararg aliases: String)= aliases.map(this::prefab)

    fun configure(appName: String? = null, android: BaseExtension) {
        _dependencies.values.forEach { it.configure(appName, android) }
    }
}

class ScopedDependenciesImpl(override val scope: DependencyScope) : ScopedDependencies {

    private val _dependencies = ArrayList<DependencyKt>()

    override operator fun plusAssign(dep: DependencyKt) {
        _dependencies.add(dep)
    }

    override operator fun plusAssign(deps: List<DependencyKt>) {
        _dependencies.addAll(deps)
    }

    fun configure(appName: String?, android: BaseExtension) {
        _dependencies.forEach { it.configure(appName, scope, android) }
    }
}
