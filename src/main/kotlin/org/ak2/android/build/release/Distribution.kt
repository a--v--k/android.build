/*
 * Copyright 2021 AK2.
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

package org.ak2.android.build.release

import com.android.build.api.artifact.ArtifactType
import com.android.build.api.variant.ApplicationVariant
import com.android.build.api.variant.impl.VariantOutputImpl
import org.ak2.android.build.AppReleaseInfo
import org.ak2.android.build.configurators.AppFlavorConfiguratorImpl
import org.ak2.android.build.configurators.config
import org.ak2.android.build.utils.findByNameAndConfigure
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.internal.file.FilePropertyFactory
import org.gradle.api.model.ObjectFactory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.bundling.Zip
import java.io.File
import javax.inject.Inject

object DistributionTasks {

    const val group = "distribution"

    fun projectDistributionTask(project: Project): ProjectDistributionTask {
        val taskName = "distribute"
        return project.tasks.maybeCreate(taskName, ProjectDistributionTask::class.java)
    }

    fun appDistributionTask(project: Project, appFlavor: AppFlavorConfiguratorImpl): AppDistributionTask {
        val taskName = "distribute${appFlavor.packageName.capitalize()}"
        return project.tasks.maybeCreate(taskName, AppDistributionTask::class.java)
    }

    fun apkDistributionTask(project: Project, v: ApplicationVariant): ApkDistributionTask {
        val taskName = "distribute${v.name.capitalize()}Apk"
        return project.tasks.maybeCreate(taskName, ApkDistributionTask::class.java)
    }

    fun proGuardMappingDistributionTask(project: Project,v: ApplicationVariant): ProGuardMappingDistributionTask {
        val taskName = "distribute${v.name.capitalize()}ProGuardMapping"
        return project.tasks.maybeCreate(taskName, ProGuardMappingDistributionTask::class.java)
    }

    fun customZipDistributionTask(project: Project, appInfo: AppReleaseInfo, archiveName : String) : CustomZipDistributionTask {
        val taskName = "distribute${appInfo.packageName.capitalize()}${archiveName.capitalize()}CustomZip"
        return project.tasks.maybeCreate(taskName, CustomZipDistributionTask::class.java)
    }
}

abstract class AbstractDistributionTask : DefaultTask() {

    init {
        group = DistributionTasks.group
    }

    @Inject
    protected abstract fun getObjectFactory(): ObjectFactory

    @Inject
    protected abstract fun getFilePropertyFactory(): FilePropertyFactory
}

abstract class ProjectDistributionTask : AbstractDistributionTask() {

    @get:OutputDirectory
    abstract val distributionRootDir: DirectoryProperty

    init {
        val rootDir = project.config.distributionRootDir ?: File("build/outputs/dist")
        distributionRootDir.set(rootDir)
    }

    @TaskAction
    fun execute() {
    }
}

abstract class AppDistributionTask : AbstractDistributionTask() {

    @get:OutputDirectory
    abstract val distributionAppDir: DirectoryProperty

    fun init(
        projectDistributionTask: ProjectDistributionTask,
        appFlavor: AppFlavorConfiguratorImpl
    ) {
        distributionAppDir.value(projectDistributionTask.distributionRootDir.dir(appFlavor.packageName))
        projectDistributionTask.dependsOn += this.name
    }

    @TaskAction
    fun execute() {
    }
}

abstract class ApkDistributionTask : AbstractDistributionTask() {

    @get:InputFile
    abstract val originalApkFile: RegularFileProperty

    @get:OutputDirectory
    abstract val distributionRootDir: DirectoryProperty

    @get:OutputFile
    abstract val targetApkFile: RegularFileProperty

    fun init(
        appDistributionTask: AppDistributionTask,
        apkConfig: AppFlavorConfiguratorImpl.ApkConfig,
        v: ApplicationVariant
    ) {
        val apkOutput = v.outputs.filterIsInstance<VariantOutputImpl>().first()

        val apkFileNameProvider = apkOutput.outputFileName;

        val originalApkDirProvider = v.artifacts.get(ArtifactType.APK)

        this.originalApkFile.value(originalApkDirProvider.map { dir -> dir.file(apkFileNameProvider.get()) })

        val distributionRootDirProvider = appDistributionTask.distributionAppDir.map { root ->
            root.dir("${apkConfig.versionName}")
        }

        this.distributionRootDir.value(distributionRootDirProvider)

        val targetApkFileProvider = distributionRootDir.map { dir ->
            dir.file("${apkConfig.apkName}.apk")
        }

        targetApkFile.value(targetApkFileProvider)

        appDistributionTask.dependsOn += this.name

        this.dependsOn += "assemble${v.name.capitalize()}"
    }

    @TaskAction
    fun execute() {
        val original = originalApkFile.get().asFile
        val dest = targetApkFile.get().asFile

        dest.parentFile.mkdirs()

        original.copyTo(dest, overwrite = true)
    }
}

abstract class ProGuardMappingDistributionTask : AbstractDistributionTask() {

    @get:InputFile
    abstract val originalMappingFile: RegularFileProperty

    @get:OutputDirectory
    abstract val distributionRootDir: DirectoryProperty

    @get:OutputFile
    abstract val targetMappingFile: RegularFileProperty

    fun init(
        appDistributionTask: AppDistributionTask,
        apkConfig: AppFlavorConfiguratorImpl.ApkConfig,
        v: ApplicationVariant
    ) {
        val originalMappingFileProvider = v.artifacts.get(ArtifactType.OBFUSCATION_MAPPING_FILE)

        this.originalMappingFile.value(originalMappingFileProvider)

        val distributionRootDirProvider = appDistributionTask.distributionAppDir.map { root ->
            root.dir("${apkConfig.versionName}")
        }

        this.distributionRootDir.value(distributionRootDirProvider)

        val targetMappingFileProvider = distributionRootDir.map { dir ->
            dir.file("mapping/${apkConfig.apkName}.mapping.txt")
        }

        targetMappingFile.value(targetMappingFileProvider)

        appDistributionTask.dependsOn += this.name
        this.dependsOn += "assemble${v.name.capitalize()}"
    }

    @TaskAction
    fun execute() {
        val original = originalMappingFile.get().asFile
        val dest = targetMappingFile.get().asFile

        dest.parentFile.mkdirs()

        original.copyTo(dest, overwrite = true)
    }
}

open class CustomZipDistributionTask : Zip() {

    fun init(appInfo: AppReleaseInfo, archName: String, destDir: String, zipConfigurator: Zip.() -> Unit) {
        val packageName = appInfo.packageName
        val version = appInfo.version.versionName

        group = DistributionTasks.group
        archiveBaseName.set(packageName)
        archiveAppendix.set(archName)
        archiveVersion.set(appInfo.version.versionName)

        let { task ->
            task.zipConfigurator()

            val distTaskName = "distribute${packageName.capitalize()}"

            project.tasks.findByNameAndConfigure<AppDistributionTask>(distTaskName) {
                dependsOn += task.name
                task.destinationDirectory.value(this.distributionAppDir.map { dir -> dir.dir("$version/$destDir") })
                println("${project.path}: add $task to $this")
            }
        }
    }
}


