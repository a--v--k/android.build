package org.ak2.android.build.configurators

import org.ak2.android.build.RootConfigurator
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler

class RootConfiguratorImpl(private val project: Project) : RootConfigurator {

    val config : ProjectConfiguration
        get() = project.config

    override fun repositories(block: RepositoryHandler.() -> Unit) {
        project.config.repositories = block
    }

    fun configure(block : RootConfigurator.() -> Unit) {
        block(this)
        project.config.repositories(project.repositories)
    }
}