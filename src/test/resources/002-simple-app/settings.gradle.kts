pluginManagement {

    val ak2AndroidBuildPluginVersion : String by settings

    repositories {
        google()
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
        gradlePluginPortal()
    }
}
