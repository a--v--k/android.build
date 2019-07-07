package org.ak2.android.build.dependencies.base

import com.android.build.gradle.BaseExtension
import org.ak2.android.build.configurators.*

class ModuleDependencyKt(private val alias: String? = null, val dependencyPath: String) : DependencyKt {

    val effectiveAlias : String
        get() = alias ?: dependencyPath.split(':').last()

    override fun configure(appName: String?, scope: DependencyScope, android: BaseExtension) {
        val project = android.androidProject

        android.addVariantConfigurator { variant ->
            if (appName == null || variant.name.startsWith(appName)) {
                println("${project.path}: Add module dependency $dependencyPath[${scope.scope}]")
                project.run {
                    dependencies.add(scope.scope, project(dependencyPath))
                }
            } else {
                println("${project.path}/${variant.name}: Skip module dependency $dependencyPath")
            }
        }
    }


}