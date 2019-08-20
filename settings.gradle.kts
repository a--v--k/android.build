rootProject.name = "android.build"

val kotlinVersion        : String by settings
val publishPluginVersion : String by settings

pluginManagement {

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