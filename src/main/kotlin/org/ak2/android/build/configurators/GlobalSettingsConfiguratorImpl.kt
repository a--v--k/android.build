package org.ak2.android.build.configurators

import org.ak2.android.build.GlobalSettingsConfigurator
import org.ak2.android.build.dependencies.base.ModuleDependencyKt
import org.gradle.api.initialization.Settings
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

class GlobalSettingsConfiguratorImpl(private val settings: Settings) : GlobalSettingsConfigurator {

    private val configured = AtomicBoolean()

    override fun module(path : String)                 { Modules.add(ModuleDependencyKt(dependencyPath = path)) }
    override fun module(alias : String, path : String) { Modules.add(ModuleDependencyKt(alias, path))           }

    fun configure(block: GlobalSettingsConfigurator.() -> Unit) {
        if (configured.compareAndSet(false, true)) {
            settings.rootProject.buildFileName = "build.gradle.kts"
            this.block()

            Modules.modules.values.forEach {
                settings.include(it.dependencyPath)
            }
        }
    }
}

object Modules {

    internal val modules = TreeMap<String, ModuleDependencyKt>()

    fun module(alias: String): ModuleDependencyKt {
        val module = modules[alias]
        return module ?: throw IllegalArgumentException("Unknown module $alias")
    }

    internal fun add(module: ModuleDependencyKt) = apply {
        println("Add module ${module.effectiveAlias}/${module.dependencyPath}")
        modules[module.effectiveAlias] = module
    }
}
