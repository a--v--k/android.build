rootProject.name = "android.build"

pluginManagement {

    val kotlinVersion        : String by settings
    val publishPluginVersion : String by settings

    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }

    plugins {
        id("com.gradle.plugin-publish") version "$publishPluginVersion"
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}



