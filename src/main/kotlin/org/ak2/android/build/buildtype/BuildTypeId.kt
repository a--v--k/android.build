package org.ak2.android.build.buildtype

import com.android.build.gradle.BaseExtension
import com.android.build.gradle.internal.dsl.BuildType

sealed class BuildTypeId(val id: String) {
    object RELEASE              : BuildTypeId("release")
    object DEBUG                : BuildTypeId("debug")
    class CUSTOM(id: String)    : BuildTypeId(id)

    fun configure(android : BaseExtension, block :  BuildType.() -> Unit) {
        android.buildTypes.maybeCreate(id).block()
    }

    companion object {
        fun of(buildTypeId: String) = when (buildTypeId) {
            RELEASE.id  -> RELEASE
            DEBUG.id    -> DEBUG
            else        -> CUSTOM(buildTypeId)
        }
    }
}

