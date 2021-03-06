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

import org.ak2.android.build.BuildConfigurator

class FlavorConfigsBuilder(private val flavor: AndroidPlatforms) : BuildConfigurator.VariantBuilder {

    private val children = LinkedHashSet<NativePlatforms>()

    override fun native(flavor: NativePlatforms) {
        children.add(flavor)
    }

    override fun native(flavors: List<NativePlatforms>) {
        children.addAll(flavors)
    }

    fun build() = if (children.isEmpty()) {
        listOf<FlavorConfig>(FlavorConfig(androidFlavor = flavor))
    } else {
        children.map { FlavorConfig(androidFlavor = flavor, nativeFlavor = it) }
    }

    override fun toString(): String {
        return "$flavor -> $children"
    }
}