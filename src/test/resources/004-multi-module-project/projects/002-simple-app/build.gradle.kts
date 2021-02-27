import org.ak2.android.build.dependencies.base.DependencyScope
import org.ak2.android.build.release.createZip
import org.gradle.kotlin.dsl.project

plugins {
    id("org.ak2.android.build")
}

androidApp {

    dependsOn {
        compileOnly += library("androidx.annotation:annotation:1.1.0")
        compileOnly += library("org.jetbrains:annotations:13.0")

        add(DependencyScope.IMPLEMENTATION, project.dependencies.project(":projects:001-simple-library"))
    }

    release {
        onRelease { appInfo ->
            createZip(appInfo, "src", "sources") {
                from(file("src")) {
                    include("main/")
                    into("src")
                }
            }
        }
    }
}
