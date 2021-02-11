include(":projects:001-simple-library")
include(":projects:002-simple-app")
include(":projects:003-app-set")

pluginManagement {

    val ak2AndroidBuildPluginVersion : String by settings

    repositories {
        google()
        jcenter()
        mavenCentral()
        gradlePluginPortal()
    }

    plugins {
        id("org.ak2.android.build") version ak2AndroidBuildPluginVersion
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        jcenter()
        gradlePluginPortal()
    }
}
