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

package org.ak2.android.build.ndk

import com.android.build.gradle.internal.dsl.CoreExternalNativeNdkBuildOptions


open class NativeOptions {

    private val args        = mutableListOf<String>()
    private val c_flags     = mutableListOf<String>()
    private val cpp_flags   = mutableListOf<String>()

    fun        args(vararg args  : String) { this.args      += args  }
    fun     c_flags(vararg flags : String) { this.c_flags   += flags }
    fun   cpp_flags(vararg flags : String) { this.cpp_flags += flags }

    fun configure(ndkBuild : CoreExternalNativeNdkBuildOptions) {
        ndkBuild.arguments.addAll(args)
        ndkBuild.getcFlags().addAll(c_flags)
        ndkBuild.cppFlags.addAll(cpp_flags)
    }
}