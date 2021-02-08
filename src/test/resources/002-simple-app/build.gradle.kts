plugins {
    id("org.ak2.android.build")
}

repositories {
    google()
    jcenter()
    mavenCentral()
}

androidApp {

    additionalManifests = project.fileTree("src/main/manifests").apply { include("*/AndroidManifest.xml") }

    dependsOn {
        compileOnly += library("androidx.annotation:annotation:1.1.0")
        compileOnly += library("org.jetbrains:annotations:13.0")
    }
}
