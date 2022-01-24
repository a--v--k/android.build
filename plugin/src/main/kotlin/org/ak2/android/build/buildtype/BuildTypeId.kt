/*
 * Copyright 2021 AK2.
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

package org.ak2.android.build.buildtype

import com.android.build.gradle.BaseExtension
import com.android.build.gradle.internal.dsl.BuildType

sealed class BuildTypeId(val id: String) {
    object RELEASE              : BuildTypeId("release")
    object DEBUG                : BuildTypeId("debug")
    class  CUSTOM(id: String)   : BuildTypeId(id)

    fun configure(android : BaseExtension, block :  BuildType.() -> Unit) {
        android.buildTypes.maybeCreate(id).block()
    }

    companion object {
        fun of(buildTypeId: String?) : BuildTypeId? = when {
            buildTypeId.isNullOrBlank()                         -> null
            buildTypeId.equals(RELEASE.id, ignoreCase = true)   -> RELEASE
            buildTypeId.equals(  DEBUG.id, ignoreCase = true)   -> DEBUG
            else                                                -> CUSTOM(buildTypeId)
        }
    }

    override fun toString() = id
}

