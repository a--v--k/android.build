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
import org.ak2.android.build.configurators.androidProject
import org.ak2.android.build.configurators.config
import org.ak2.android.build.dependencies.base.DependencyKt
import org.ak2.android.build.dependencies.base.DependencyScope
import org.ak2.android.build.dependencies.base.LibraryDependencyKt

object ThirdParties {

    fun AndroidAnnotations(version: String) = object : DependencyKt {

        override fun configure(appName: String?, scope: DependencyScope, android: BaseExtension) {
            android.androidProject.dependencies.run {
                if (android.androidProject.config.useKotlinInProd || android.androidProject.config.useKotlinInProd) {
                    add(DependencyScope.KAPT.scopeName(appName), "org.androidannotations:androidannotations:$version")
                } else {
                    add(DependencyScope.ANNOTATION_PROCESSOR.scopeName(appName), "org.androidannotations:androidannotations:$version")
                }
                 add(scope.scopeName(appName), "org.androidannotations:androidannotations-api:$version")
            }
        }
    }

    object Rx {

        fun rxRelay(version: String) = LibraryDependencyKt("com.jakewharton.rxrelay2:rxrelay:$version")

        fun rxAndroid(version: String) = LibraryDependencyKt("io.reactivex.rxjava2:rxandroid:$version")

        fun rxJava(version: String) = LibraryDependencyKt("io.reactivex.rxjava2:rxjava:$version")

        fun rxBinding(version: String) = LibraryDependencyKt("com.jakewharton.rxbinding2:rxbinding:$version")

        fun rxBindingAppcompat(version: String) = LibraryDependencyKt("com.jakewharton.rxbinding2:rxbinding-appcompat-v7:$version")

        fun rxBindingDesign(version: String) = LibraryDependencyKt("com.jakewharton.rxbinding2:rxbinding-design:$version")

        fun rxBindingSupport(version: String) = LibraryDependencyKt("com.jakewharton.rxbinding2:rxbinding-support-v4:$version")

        fun rxBindingRecyclerView(version: String) = LibraryDependencyKt("com.jakewharton.rxbinding2:rxbinding-recyclerview-v7:$version")
    }
}