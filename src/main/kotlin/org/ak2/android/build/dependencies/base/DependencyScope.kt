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

package org.ak2.android.build.dependencies.base

val DependencyScope.Companion.standardScopes : List<DependencyScope> by lazy {
    listOf(
        DependencyScope.API,
        DependencyScope.IMPLEMENTATION,
        DependencyScope.COMPILE_ONLY,
        DependencyScope.TEST,
        DependencyScope.TEST_RUNTIME,
        DependencyScope.ANDROID_TEST,
        DependencyScope.ANDROID_TEST_RUNTIME,
        DependencyScope.APT,
        DependencyScope.KAPT
    )
}

sealed class DependencyScope(val scope: String) : Comparable<DependencyScope> {

    object API                  : DependencyScope("api")
    object IMPLEMENTATION       : DependencyScope("implementation")
    object COMPILE_ONLY         : DependencyScope("compileOnly")
    object TEST                 : DependencyScope("testImplementation")
    object TEST_RUNTIME         : DependencyScope("testRuntime")
    object ANDROID_TEST         : DependencyScope("androidTestImplementation")
    object ANDROID_TEST_RUNTIME : DependencyScope("androidTestRuntime")
    object APT                  : DependencyScope("annotationProcessor")
    object KAPT                 : DependencyScope("kapt");

    class CustomDependency(scope: String) : DependencyScope(scope)

    fun scopeName(appName: String?) = if (appName != null) appName + scope.capitalize() else scope

    override fun compareTo(other: DependencyScope): Int {
        return this.scope.compareTo(other.scope)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other is DependencyScope) {
            return this.scope == other.scope
        }

        return false
    }

    override fun hashCode(): Int {
        return scope.hashCode()
    }

    override fun toString(): String {
        return "DependencyScope($scope)"
    }

    companion object {

        fun of(scope: String) = when (scope) {
            API.scope                   -> API
            IMPLEMENTATION.scope        -> IMPLEMENTATION
            COMPILE_ONLY.scope          -> COMPILE_ONLY
            TEST.scope                  -> TEST
            TEST_RUNTIME.scope          -> TEST_RUNTIME
            ANDROID_TEST.scope          -> ANDROID_TEST
            ANDROID_TEST_RUNTIME.scope  -> ANDROID_TEST_RUNTIME
            APT.scope                   -> APT
            KAPT.scope                  -> KAPT
            else                        -> CustomDependency(scope)
        }
    }
}


