package org.ak2.android.build.dependencies.base

import com.android.build.gradle.BaseExtension
import org.ak2.android.build.configurators.androidProject

class LibraryDependencyKt(val dependency: String) : DependencyKt {

    override fun configure(appName: String?, scope: DependencyScope, android: BaseExtension) {
        val project = android.androidProject
        val depType = scope.scopeName(appName)

        println("${project.path}: Add library dependency $dependency[$depType]")
        project.dependencies.add(depType, dependency)
    }

}