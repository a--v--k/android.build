package org.ak2.android.build.configurators

import org.ak2.android.build.RootConfigurator
import org.ak2.android.build.tasks.HardCleanTask.Companion.addHardCleanTask
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler

class RootConfiguratorImpl(override val project: Project) : RootConfigurator {

    override val config : ProjectConfiguration
        get() = project.config

    override fun addRepositories(block: RepositoryHandler.() -> Unit) {
        config.repositories = block
    }

    fun configure(block : RootConfigurator.() -> Unit) {
        project.addHardCleanTask()

        block(this)
        config.repositories(project.repositories)
    }
}