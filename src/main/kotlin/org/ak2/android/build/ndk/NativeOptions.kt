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