package org.ak2.android.build.configurators

import com.android.build.gradle.AppExtension
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.api.BaseVariant
import org.ak2.android.build.extras.*
import org.ak2.android.build.flavors.*
import org.gradle.api.GradleException
import org.gradle.api.Project
import java.util.stream.Collectors

var BaseExtension.androidProject: Project
    get() = getExtra<Project>(this, "project") as Project
    set(value) = setExtra(this, "project", value)

var Project.androidExtension: BaseExtension
    get() = getExtra<BaseExtension>(this, "androidExtension") as BaseExtension
    set(value) = setExtra(this, "androidExtension", value)

val Project.config: ProjectConfiguration
    get() {
        val parent = this.parent
        return putExtraIfAbsent(this, "projectConfig") {
            if (parent == null) {
                RootConfiguration(this)
            } else {
                InnerProjectConfiguration(this, this.projectDir, parent.config)
            }
        }
    }

fun Project.link(android: BaseExtension) {
    this.androidExtension = android
    android.androidProject = this
}

fun BaseExtension.getVariantConfigs(): LinkedHashMap<String, VariantConfig> {
    return putExtraIfAbsent(this, "Variants") { LinkedHashMap() }
}

fun BaseExtension.addVariantValidator(validator: VariantValidator) {
    getVariantProcessor() += validator
}

fun BaseExtension.addVariantConfigurator(configurator: VariantConfigurator) {
    getVariantProcessor() += configurator
}

fun BaseExtension.addPostConfigurator(configurator: VariantPostConfigurator) {
    getVariantProcessor() += configurator
}

fun BaseExtension.getVariantProcessor(): VariantProcessor = putExtraIfAbsent(this, "VariantProcessor") { VariantProcessor(this.androidProject) }

fun BaseExtension.getVariantNames(): Set<String> {
    return this.getVariants().stream()
            .map { it.name }
            .collect(Collectors.toSet())
}

fun BaseExtension.getVariants(): MutableList<BaseVariant> {
    if (this is LibraryExtension) {
        return libraryVariants.stream().collect(Collectors.toList())
    } else if (this is AppExtension) {
        return applicationVariants.stream().collect(Collectors.toList())
    }
    throw GradleException("Unknown configurators extension: $this")
}


private fun BaseExtension.getValidators(): MutableList<VariantValidator> {
    return putExtraIfAbsent(this, "VariantValidatorList") { ArrayList() }
}

private fun BaseExtension.getConfigurators(): MutableList<VariantConfigurator> {
    return putExtraIfAbsent(this, "VariantConfiguratorList") { ArrayList() }
}

private fun BaseExtension.getPostConfigurators(): MutableList<VariantPostConfigurator> {
    return putExtraIfAbsent(this, "VariantPostConfiguratorList") { ArrayList() }
}

fun getDefaultAppName(projectName: String) =
        StringBuilder()
                .append(projectName[0].toLowerCase())
                .append(projectName.substring(1, projectName.length))
                .toString()


fun getDependencyName(variantName: String) =
        StringBuilder()
                .append(variantName[0].toLowerCase())
                .append(variantName.substring(1, variantName.length))
                .append("Specific")
                .toString()
