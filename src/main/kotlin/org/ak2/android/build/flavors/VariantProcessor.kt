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
import com.android.build.gradle.api.BaseVariant
import org.ak2.android.build.configurators.androidExtension
import org.ak2.android.build.configurators.getVariants
import org.gradle.api.Project

typealias VariantValidator = (VariantFilter) -> Boolean
typealias VariantConfigurator = (VariantFilter) -> Unit
typealias VariantPostConfigurator = (BaseVariant) -> Unit

class VariantProcessor(val project: Project) {

    val variantValidators = ArrayList<VariantValidator>();
    val variantConfigurators = ArrayList<VariantConfigurator>();
    val variantPostConfigurators = ArrayList<VariantPostConfigurator>();

    init {
        project.androidExtension.variantFilter {
            doFilter(this)
        }

        project.afterEvaluate {
            doPostConfiguration()
        }
    }

    @JvmName("addValidator")
    operator fun plusAssign(validator: VariantValidator) {
        variantValidators.add(validator);
    }

    @JvmName("addConfigurator")
    operator fun plusAssign(configurator: VariantConfigurator) {
        variantConfigurators.add(configurator);
    }

    @JvmName("addPostConfigurator")
    operator fun plusAssign(postConfigurator: VariantPostConfigurator) {
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

    fun doPostConfiguration() {
        println("${project.path}: Configure variants...")
        project.androidExtension.getVariants().forEach { variant ->
            println("${project.path}: Configure variant ${variant.name}...")
            variantPostConfigurators.forEach { it(variant) }
        }
    }

    fun validate(variant: VariantFilter): Boolean {
        return variantValidators.stream().allMatch { it(variant) }
    }

}