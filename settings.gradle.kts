rootProject.name = "android.build"

pluginManagement {

    val kotlinVersion        : String by settings
    val publishPluginVersion : String by settings

    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        jcenter()
    }

    plugins {
        id("org.jetbrains.kotlin.jvm") version "$kotlinVersion"
        id("com.gradle.plugin-publish") version "$publishPluginVersion"
    }
}