rootProject.name = "android.build.plugin.project"

includeBuild("plugin")

include(":samples:001-simple-library")
include(":samples:002-simple-app")
include(":samples:003-app-set")

include(":samples:004-multi-module-project:java-library")
include(":samples:004-multi-module-project:standalone-log")
include(":samples:004-multi-module-project:standalone-jni")
include(":samples:004-multi-module-project:app")

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
