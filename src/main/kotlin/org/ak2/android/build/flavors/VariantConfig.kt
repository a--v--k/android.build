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

data class VariantConfig(val buildType: String? = null, val appFlavor: AppFlavorConfiguratorImpl? = null, val androidFlavor: AndroidPlatforms?, val nativeFlavor: NativePlatforms? = null) {

    val name = lazy {
        VariantNameBuilder()
                .append(if (appFlavor == null) "" else appFlavor.name)
                .append(androidFlavor)
                .append(nativeFlavor)
                .append(buildType)
                .build()
    }

    val dimensions = lazy {
        toFlavors().map(Flavor::dimensionName).toList()
    }

    fun toFlavors() = sequenceOf(appFlavor, androidFlavor, nativeFlavor).filterNotNull()

    fun toFlavorConfig() = FlavorConfig(androidFlavor, nativeFlavor)

    override fun toString(): String {
        return name.value;
    }
}

fun Collection<VariantConfig>.getDimensions() = this.asSequence().flatMap { it.dimensions.value.asSequence() }.distinct().toSet()

fun Collection<VariantConfig>.toFlavors() = this.asSequence().flatMap { it.toFlavors() }.distinct()

