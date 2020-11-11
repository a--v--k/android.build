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

import com.android.build.api.dsl.ExternalNativeNdkBuildOptions


open class NativeOptions {

    private val _args        = mutableListOf<String>()
    private val _c_flags     = mutableListOf<String>()
    private val _cpp_flags   = mutableListOf<String>()

    val args : List<String>
        get() = _args

    val c_flags : List<String>
        get() = _c_flags

    val cpp_flags : List<String>
        get() = _cpp_flags


    fun        args(vararg args  : String) { this._args      += args  }
    fun     c_flags(vararg flags : String) { this._c_flags   += flags }
    fun   cpp_flags(vararg flags : String) { this._cpp_flags += flags }

    fun configure(ndkBuild : ExternalNativeNdkBuildOptions) {
        ndkBuild.arguments.addAll(args)
        ndkBuild.cFlags.addAll(c_flags)
        ndkBuild.cppFlags.addAll(cpp_flags)
    }
}