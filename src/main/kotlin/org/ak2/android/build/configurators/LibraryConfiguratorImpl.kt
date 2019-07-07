package org.ak2.android.build.configurators

import org.ak2.android.build.BuildConfigurator.VariantBuilder
import org.ak2.android.build.DependenciesConfigurator.DependencyBuilder
import org.ak2.android.build.LibraryConfigurator
import org.ak2.android.build.NativeConfigurator.NativeOptionsBuilder
import org.ak2.android.build.flavors.AndroidPlatforms
import org.ak2.android.build.flavors.FlavorConfigs
import org.ak2.android.build.flavors.VariantConfig
import org.gradle.kotlin.dsl.KotlinBuildScript

class LibraryConfiguratorImpl(project: KotlinBuildScript) : BaseAndroidConfiguratorKt(project, "com.configurators.library"), LibraryConfigurator {

    private val flavors = FlavorConfigs()

    override fun buildFor(flavor: AndroidPlatforms, innerBuilder: VariantBuilder.() -> Unit) { flavors.buildFor(flavor, innerBuilder) }

    override fun     dependsOn(block: DependencyBuilder.()    -> Unit) = knownDependencies.block()
    override fun nativeOptions(block: NativeOptionsBuilder.() -> Unit) = native.block()

    fun configure(block: LibraryConfiguratorImpl.() -> Unit) = configureProject { this.block() }

    override fun buildVariants(variantConfigs: LinkedHashMap<String, VariantConfig>) {
        val buildTypes = project.androidExtension.buildTypes.map { it.name }
        flavors.toFlavors()
                .flatMap { flavor -> buildTypes.asSequence().map { buildType ->
                    VariantConfig(buildType = buildType, androidFlavor = flavor.androidFlavor, nativeFlavor = flavor.nativeFlavor)
                    }
                }
                .forEach {
                    variantConfigs.put(it.name.value, it)
                }
    }
}