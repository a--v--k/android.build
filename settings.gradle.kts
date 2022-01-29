rootProject.name = "android.build.plugin.project"

includeBuild("plugin")

include(":samples:001-simple-library")
include(":samples:002-simple-app")
include(":samples:003-app-set")

pluginManagement {

    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }

    plugins {
        id("org.ak2.android.build")
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
