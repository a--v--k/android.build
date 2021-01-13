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

data class CommonNdkOptions (
    val appType     : AppLevel  = AppLevel.RELEASE,
    val debug       : Debug     = Debug.DISABLED,
    val linkerFlags : String    = "APP_LDFLAGS+=-Wl,-s"
)

data class CppNdkOptions(
    val stlLib      : StlType       = StlType.STATIC,
    val cppStandard : CppStandard   = CppStandard.STD_17,
    val rtti        : RTTI          = RTTI.ENABLED,
    val exceptions  : Excepions     = Excepions.ENABLED
)

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

    fun        args(vararg args  : NativeFlag) { this._args      += args.map  { it.flag } }
    fun     c_flags(vararg flags : NativeFlag) { this._c_flags   += flags.map { it.flag } }
    fun   cpp_flags(vararg flags : NativeFlag) { this._cpp_flags += flags.map { it.flag } }

    fun apply(options: NativeOptions) {
        this._args      += options.args
        this._c_flags   += options.c_flags
        this._cpp_flags += options.cpp_flags
    }

    fun configure(ndkBuild : ExternalNativeNdkBuildOptions) {
        ndkBuild.arguments.addAll(args)
        ndkBuild.cFlags.addAll(c_flags)
        ndkBuild.cppFlags.addAll(cpp_flags)
    }
}

data class NdkOptions (
    val common: CommonNdkOptions = CommonNdkOptions(),
    val optLevel: OptLevel = OptLevel.LEVEL_3,
    val cpp: CppNdkOptions = CppNdkOptions(),
    val other : NativeOptions = NativeOptions()
)
