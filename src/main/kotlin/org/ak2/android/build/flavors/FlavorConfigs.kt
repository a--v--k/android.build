package org.ak2.android.build.flavors

import org.ak2.android.build.BuildConfigurator
import org.ak2.android.build.BuildConfigurator.VariantBuilder

class FlavorConfigs : BuildConfigurator {

    private val flavors = LinkedHashMap<String, FlavorConfig>()

    override fun buildFor(flavor: AndroidPlatforms, innerBuilder: VariantBuilder.() -> Unit) {
        FlavorConfigsBuilder(flavor).apply(innerBuilder).build().forEach {
            flavors.put(it.name.value, it)
        }
    }

    fun dimensions(): Set<String> {
        return flavors.values.asSequence().flatMap { it.dimensions.value.asSequence() }.toSet()
    }

    fun toFlavors() = flavors.values.asSequence()
}

