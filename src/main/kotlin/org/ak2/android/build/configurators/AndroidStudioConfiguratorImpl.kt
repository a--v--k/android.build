/*
 * Copyright 2020 AK2.
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

import org.ak2.android.build.AndroidStudioConfigurator
import org.ak2.android.build.buildtype.BuildTypeId
import org.ak2.android.build.flavors.AndroidPlatforms
import org.ak2.android.build.flavors.NativePlatforms
import org.gradle.api.Project

class AndroidStudioConfiguratorImpl : AndroidStudioConfigurator {

    override var defaultBuildType       : BuildTypeId?      = null
    override var defaultAndroidPlatform : AndroidPlatforms? = null
    override var defaultNativePlatform  : NativePlatforms?  = null

    fun configure(block : AndroidStudioConfigurator.() -> Unit) {
        this.block()
    }
}