/*
 * Copyright 2020 AK2.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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