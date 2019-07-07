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

