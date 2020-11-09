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
import java.io.File
import java.util.logging.Logger
import javax.inject.Inject

fun Project?.addHardCleanTask(): HardCleanTask? = takeIf { this != null }?.run {
    tasks.maybeCreate("hardClean", HardCleanTask::class.java).apply {
        parent.addHardCleanTask()?.dependsOn(this)
    }
}

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
                toClean.set(inputToClean)
            }
        }
    }
}

interface HardCleanParameters : WorkParameters {
    val toClean: Property<File>
}

abstract class DeleteFileOrDirectory @Inject constructor(
    private val fileSystemOperations: FileSystemOperations,
    private val logger: Logger,
    private val project: Project
) : WorkAction<HardCleanParameters> {

    override fun execute() {
        logger.info("${project.path} remove ${parameters.toClean}...")
        fileSystemOperations.delete {
            this.delete(parameters.toClean)
        }
    }
}

