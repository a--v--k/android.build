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

import com.android.build.gradle.BaseExtension
import org.ak2.android.build.configurators.androidProject
import org.ak2.android.build.configurators.config

open class GoogleSupportDependencyKt(private val dependency : String) : DependencyKt {

    override fun configure(appName: String?, scope: DependencyScope, android: BaseExtension) {
        val project = android.androidProject
        val depType = scope.scopeName(appName)

        val version = project.config.supportLibraryVersion
        require(version.isNotEmpty()) { "supportLibraryVersion is not set" }

        println("${project.path}: Add google support dependency $dependency:$version[$depType]")
        project.dependencies.add(depType, "$dependency:$version")
    }
}

class GoogleSupportAnnotations : GoogleSupportDependencyKt("com.android.support:support-annotations")
{
    override fun configure(appName: String?, scope: DependencyScope, android: BaseExtension) {
        super.configure(appName, DependencyScope.COMPILE_ONLY, android)
        LibraryDependencyKt("org.jetbrains:annotations:13.0").configure(appName, DependencyScope.COMPILE_ONLY, android)
    }
}

open class ContraintLayoutDependencyKt(private val dependency : String) : DependencyKt {

    override fun configure(appName: String?, scope: DependencyScope, android: BaseExtension) {
        val project = android.androidProject
        val depType = scope.scopeName(appName)

        val version = project.config.constraintLayoutVersion
        require(version.isNotEmpty()) { "constraintLayoutVersion is not set" }

        println("${project.path}: Add google support dependency $dependency:$version[$depType]")
        project.dependencies.add(depType, "$dependency:$version")
    }
}