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

import org.ak2.android.build.GlobalSettingsConfigurator
import org.ak2.android.build.dependencies.base.ModuleDependencyKt
import org.gradle.api.initialization.Settings
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

class GlobalSettingsConfiguratorImpl(private val settings: Settings) : GlobalSettingsConfigurator {

    override fun module(path: String) {
        add(ModuleDependencyKt(dependencyPath = path))
    }

    override fun module(alias: String, path: String) {
        add(ModuleDependencyKt(alias, path))
    }

    fun configure(block: GlobalSettingsConfigurator.() -> Unit) {
        if (configured.compareAndSet(false, true)) {
            println("Configure global settings...")
            settings.rootProject.buildFileName = "build.gradle.kts"
            this.block()

            modules.values.forEach {
                settings.include(it.dependencyPath)
            }
        } else {
            println("Skipping global settings...")
        }
    }


    companion object {

        internal val configured = AtomicBoolean()
        internal val modules = TreeMap<String, ModuleDependencyKt>()

        fun module(alias: String): ModuleDependencyKt {
            if (configured.get()) {
                val module = modules[alias]
                return module ?: throw IllegalArgumentException("Unknown module $alias")
            } else {
                // Fallback dependency creation if global {} was not called from settings.gradle.kts
                return modules.computeIfAbsent(alias) { ModuleDependencyKt(dependencyPath = alias) }
            }
        }

        internal fun add(module: ModuleDependencyKt) {
            println("Add module ${module.effectiveAlias}/${module.dependencyPath}")
            modules[module.effectiveAlias] = module
        }
    }
}
