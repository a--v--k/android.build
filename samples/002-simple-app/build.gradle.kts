import org.ak2.android.build.dependencies.base.DependencyScope
import org.ak2.android.build.release.createZip

plugins {
    id("org.ak2.android.build")
}

androidApp {

    additionalManifests = fileTree("src/main/manifests").apply { include("*/AndroidManifest.xml") }

    dependsOn {
        compileOnly += library("androidx.annotation:annotation:1.1.0")
        compileOnly += library("org.jetbrains:annotations:13.0")
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
