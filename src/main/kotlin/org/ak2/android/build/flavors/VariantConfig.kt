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

import org.ak2.android.build.buildtype.BuildTypeId
import org.ak2.android.build.configurators.AppFlavorConfiguratorImpl

data class VariantConfig(val buildType: BuildTypeId, val appFlavor: AppFlavorConfiguratorImpl? = null, val androidFlavor: AndroidPlatforms? = null, val nativeFlavor: NativePlatforms? = null) {

    val name = lazy {
        VariantNameBuilder()
                .append(appFlavor?.name ?: "")
                .append(androidFlavor)
                .append(nativeFlavor)
                .append(buildType.id)
                .build()
    }

    val suffix = lazy {
        sequenceOf(androidFlavor, nativeFlavor)
            .filterNotNull()
            .map { it.name.toLowerCase() }
            .joinToString(separator = "-")
    }

    val dimensions = lazy {
        toFlavors().map(Flavor::dimensionName).toList()
    }

    val enabled : Boolean
        get() = appFlavor?.enabled ?: true

    fun hasFlavors() = toFlavors().any()

    fun toFlavors() = sequenceOf(appFlavor, androidFlavor, nativeFlavor).filterNotNull()

    fun toFlavorConfig() = FlavorConfig(androidFlavor, nativeFlavor)

    override fun toString(): String {
        return name.value;
    }
}

fun Collection<VariantConfig>.getDimensions() = this.asSequence().flatMap { it.dimensions.value.asSequence() }.distinct().toSet()

fun Collection<VariantConfig>.toFlavors() = this.asSequence().flatMap { it.toFlavors() }.distinct()

