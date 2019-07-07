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