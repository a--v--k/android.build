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

import com.android.build.api.variant.VariantFilter
import com.android.build.api.variant.Variant
import org.gradle.api.Project

typealias VariantValidator = (VariantFilter) -> Boolean
typealias VariantConfigurator = (VariantFilter) -> Unit
typealias VariantPostConfigurator = (Variant) -> Unit

class VariantProcessor(val project: Project) {

    val variantValidators = ArrayList<VariantValidator>();
    val variantConfigurators = ArrayList<VariantConfigurator>();
    val variantPostConfigurators = ArrayList<VariantPostConfigurator>();

    fun addValidator(validator: VariantValidator) {
        variantValidators.add(validator);
    }

    fun addConfigurator(configurator: VariantConfigurator) {
        variantConfigurators.add(configurator);
    }

    fun addPostConfigurator(postConfigurator: VariantPostConfigurator) {
        variantPostConfigurators.add(postConfigurator);
    }

    fun doFilter(variant: VariantFilter) {
        if (validate(variant)) {
            println("${project.path}: Add variant ${variant.name}...")
            variantConfigurators.forEach { it(variant) }
        } else {
            println("${project.path}: Disable variant ${variant.name}")
            variant.ignore = true
        }
    }

    fun onVariantProperties(variant: Variant) {
        println("${project.path}: Configure variant properties ${variant.name}...")
            variantPostConfigurators.forEach { it(variant) }
    }

    fun validate(variant: VariantFilter): Boolean {
        return variantValidators.stream().allMatch { it(variant) }
    }

}