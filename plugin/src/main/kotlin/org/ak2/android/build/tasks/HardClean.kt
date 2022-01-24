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

package org.ak2.android.build.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.file.FileSystemOperations
import org.gradle.api.provider.Property
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.workers.WorkAction
import org.gradle.workers.WorkParameters
import org.gradle.workers.WorkerExecutor
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import javax.inject.Inject

abstract class HardCleanTask @Inject constructor(private val workerExecutor: WorkerExecutor) :
    DefaultTask() {

    @get:PathSensitive(PathSensitivity.RELATIVE)
    @get:InputFiles
    abstract val toClean: Property<FileCollection>;

    init {
        group = "build"
        toClean.set(project.files(".cxx", "build"))
    }

    @TaskAction
    fun execute() {
        logger.info("${project.path} run HardClean task...")
        val workQueue = workerExecutor.noIsolation()
        toClean.orNull?.files?.forEach { inputToClean ->
            workQueue.submit(DeleteFileOrDirectory::class.java) {
                affectedProject.set(project.path)
                toClean.set(inputToClean)
            }
        }
    }

    companion object {
        const val DEFAULT_TASK_NAME = "hardClean"

        fun Project?.addHardCleanTask(): HardCleanTask? = takeIf { this != null }?.run {
            tasks.maybeCreate(DEFAULT_TASK_NAME, HardCleanTask::class.java).apply {
                parent.addHardCleanTask()?.dependsOn(this)
            }
        }
    }
}

interface HardCleanParameters : WorkParameters {
    val affectedProject: Property<String>
    val toClean: Property<File>
}

abstract class DeleteFileOrDirectory @Inject constructor(
    private val fileSystemOperations: FileSystemOperations
) : WorkAction<HardCleanParameters> {

    override fun execute() {
        with(parameters) {
            toClean.orNull?.takeIf { it.exists() }?.let { dirToClean ->
                logger.info("${affectedProject.get()}: remove ${dirToClean}...")
                fileSystemOperations.delete {
                    this.delete(dirToClean)
                }
            }
        }
    }

    companion object {
        val logger : Logger  = LoggerFactory.getLogger(DeleteFileOrDirectory::class.java)
    }
}

