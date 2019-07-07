package org.ak2.android.build.dependencies.base

import com.android.build.gradle.BaseExtension
import org.ak2.android.build.configurators.androidProject

class LocalJarDependencyKt(val localPath: String) : DependencyKt {

    override fun configure(appName: String?, scope: DependencyScope, android: BaseExtension) {
        val project = android.androidProject

        require(project.file(localPath).exists()) { "Local library does not exit: ${project.name}/$localPath" }

        val depType = scope.scopeName(appName)

        println("${project.path}: Add local dependency $localPath[$depType]")
        project.dependencies.add(depType, project.files(localPath))
    }
}