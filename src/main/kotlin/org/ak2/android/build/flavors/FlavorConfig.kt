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

package org.ak2.android.build.flavors

import org.ak2.android.build.configurators.AppFlavorConfiguratorImpl

data class FlavorConfig(val androidFlavor: AndroidPlatforms?, val nativeFlavor: NativePlatforms? = null) {

    val name = lazy {
        VariantNameBuilder().append(androidFlavor).append(nativeFlavor).build()
    }

    val dimensions = lazy {
        sequenceOf<Flavor?>(androidFlavor, nativeFlavor).filterNotNull().map(Flavor::dimensionName).toSet()
    }

    fun toVariantConfig(buildType: String? = null, appFlavor: AppFlavorConfiguratorImpl? = null) = VariantConfig(buildType, appFlavor, androidFlavor, nativeFlavor)

    override fun toString(): String {
        return name.value;
    }
}