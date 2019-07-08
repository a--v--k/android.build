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

package org.ak2.android.build.configurators

import java.util.*

class AppVersionKt(
        var versionName: String? = null,
        var majorVersionCode: Int = 0,
        var minorVersionCode: Int = 0
) {

    val versionCode: Int
        get() = majorVersionCode * 1000 + minorVersionCode

    fun fromProperties(p: Properties) = apply {
        versionName      = p.getProperty("package.version.name")
        majorVersionCode = Integer.parseInt(p.getProperty("package.version.code.major"))
        minorVersionCode = Integer.parseInt(p.getProperty("package.version.code.minor"))
    }
}