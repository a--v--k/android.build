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
                affectedProject.set(project)
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
    val affectedProject: Property<Project>
    val toClean: Property<File>
}

abstract class DeleteFileOrDirectory @Inject constructor(
    private val fileSystemOperations: FileSystemOperations
) : WorkAction<HardCleanParameters> {

    override fun execute() {
        with(parameters) {
            toClean.orNull?.takeIf { it.exists() }?.let { dirToClean ->
                logger.info("${affectedProject.get().path} remove ${dirToClean}...")
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

