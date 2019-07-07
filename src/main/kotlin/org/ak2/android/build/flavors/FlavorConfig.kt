package org.ak2.android.build.flavors

import org.ak2.android.build.configurators.AppFlavorConfiguratorImpl

data class FlavorConfig(val androidFlavor: AndroidPlatforms?, val nativeFlavor: NativePlatforms? = null) {

    val name = lazy {
        VariantNameBuilder().append(androidFlavor).append(nativeFlavor).build()
    }

    val dimensions = lazy {
        sequenceOf<Flavor?>(androidFlavor, nativeFlavor).filterNotNull().map(Flavor::dimensionName).toSet()
    }

    fun toVariantConfig(buildType: String? = null, appFlavor: AppFlavorConfiguratorImpl? = null) = VariantConfig(buildType, appFlavor, androidFlavor, nativeFlavor)

    override fun toString(): String {
        return name.value;
    }
}