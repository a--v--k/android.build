package org.ak2.android.build.configurators

import org.ak2.android.build.BaseAppConfigurator.AppDebugConfigurator
import org.ak2.android.build.BaseAppConfigurator.AppReleaseConfigurator
import org.ak2.android.build.BuildConfigurator
import org.ak2.android.build.flavors.FlavorConfigs
import org.ak2.android.build.flavors.VariantConfig
import org.ak2.android.build.release.ReleaseCallback
import org.ak2.android.build.release.addReleaseCallback

sealed class AppFlavorBuildTypeConfigurator(val buildType: String, val appFlavor: AppFlavorConfiguratorImpl, val localFlavors : FlavorConfigs = FlavorConfigs()) : BuildConfigurator by localFlavors {

    fun buildVariants(variantConfigs: LinkedHashMap<String, VariantConfig>) {
        localFlavors.toFlavors()
                .map {
                    it.toVariantConfig(this.buildType, appFlavor)
                }
                .forEach {
                    variantConfigs.put(it.name.value, it)
                }
    }

    class AppReleaseConfiguratorImpl(appFlavor: AppFlavorConfiguratorImpl) : AppFlavorBuildTypeConfigurator("release", appFlavor), AppReleaseConfigurator {

        override fun onRelease(releaseCallback: ReleaseCallback) {
            appFlavor.parent.project.addReleaseCallback(appFlavor.name, releaseCallback)
        }
    }

    class AppDebugConfiguratorImpl(appFlavor: AppFlavorConfiguratorImpl) : AppFlavorBuildTypeConfigurator("debug", appFlavor), AppDebugConfigurator
}