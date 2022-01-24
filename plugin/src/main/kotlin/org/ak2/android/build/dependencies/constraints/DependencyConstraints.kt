package org.ak2.android.build.dependencies.constraints

import org.ak2.android.build.dependencies.base.DependencyScope
import org.ak2.android.build.extras.putExtraIfAbsent
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.DependencyConstraintHandler

val Project.dependencyConstraints : MutableMap<String, DependencyConstraint>
    get() = putExtraIfAbsent(rootProject, "dependencyConstraints") { mutableMapOf() }

enum class VersionConstraintType {
    REQUIRE, PREFER, STRICT
}

infix fun String.requireVersion(version : String)
        = DependencyConstraint(this, VersionConstraintType.REQUIRE, version)

infix fun String.preferVersion(version : String)
        = DependencyConstraint(this, VersionConstraintType.PREFER, version)

infix fun String.strictVersion(version : String)
        = DependencyConstraint(this, VersionConstraintType.STRICT, version)

class DependencyConstraint(
    val dependency: String,
    val type: VersionConstraintType,
    val version: String
) {
    fun apply(handler : DependencyConstraintHandler, scope : DependencyScope) {
        val versionToUse = version
        handler.add(scope.scope, dependency) {
            version {
                when(type) {
                    VersionConstraintType.REQUIRE -> require(versionToUse)
                    VersionConstraintType.PREFER -> prefer(versionToUse)
                    VersionConstraintType.STRICT -> strictly(versionToUse)
                }
            }
        }
    }
}

