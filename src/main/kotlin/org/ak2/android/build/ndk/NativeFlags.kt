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

interface NativeFlag {
    val flag: String
}

sealed class OptLevel(override val flag: String) : NativeFlag {
    object LEVEL_0 : OptLevel("-O0")
    object LEVEL_1 : OptLevel("-O1")
    object LEVEL_Z : OptLevel("-Oz")
    object LEVEL_2 : OptLevel("-O2")
    object LEVEL_3 : OptLevel("-O3")

    class CUSTOM(flag: String) : OptLevel(flag)
}

sealed class AppLevel(override val flag: String) : NativeFlag {
    object RELEASE : AppLevel("APP_OPTIM=release")
    object DEBUG   : AppLevel("APP_OPTIM=debug")
}

sealed class Debug(override val flag: String) : NativeFlag {
    object ENABLED  : Debug("NDEBUG=1")
    object DISABLED : Debug("NDEBUG=0")
}

sealed class StlType(override val flag: String) : NativeFlag {
    object STATIC : StlType("APP_STL=c++_static")
    object SHARED : StlType("APP_STL=c++_shared")
    object NONE   : StlType("APP_STL=none")
    object SYSTEM : StlType("APP_STL=system")

    class CUSTOM(flag: String) : StlType(flag)
}

sealed class CppStandard(override val flag: String) : NativeFlag {
    object STD_11 : CppStandard("-std=c++11")
    object STD_14 : CppStandard("-std=c++14")
    object STD_17 : CppStandard("-std=c++17")
    object STD_2A : CppStandard("-std=c++2a")

    class CUSTOM(flag: String) : CppStandard(flag)
}

sealed class RTTI(override val flag: String) : NativeFlag {
    object ENABLED  : RTTI("-frtti")
    object DISABLED : RTTI("-fno_rtti")
}

sealed class Excepions(override val flag: String) : NativeFlag {
    object ENABLED  : Excepions("-fexceptions")
    object DISABLED : Excepions("-fno-exceptions")
}

data class LinkedOptions(val flags : List<String> = emptyList()) : NativeFlag {

    constructor(vararg flags : String) : this(listOf(*flags))

    override val flag: String
        get() = if (flags.isNotEmpty()) {
            flags.joinToString(separator = ",", prefix = "APP_LDFLAGS+=")
        } else {
            ""
        }
}
