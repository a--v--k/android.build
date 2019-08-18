import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "org.ak2"
version = "3.4.1-ga-11"

buildscript {
    repositories {
        google()
        jcenter()
        mavenCentral()
        gradlePluginPortal()
    }

    dependencies {
        classpath( "com.android.tools.build:gradle:3.4.1")
        classpath ("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.31")
    }
}

plugins {
    `kotlin-dsl`
    kotlin("jvm") version "1.3.31"
    id("com.gradle.plugin-publish") version "0.10.1"
}

repositories {
    google()
    mavenCentral()
    jcenter()
    gradlePluginPortal()
}

dependencies {
    implementation(gradleApi())
    implementation(localGroovy())
    implementation( "com.android.tools.build:gradle:3.4.1")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.31")
    implementation("org.jacoco:org.jacoco.core:0.7.9")
    implementation("org.languagetool:languagetool-core:4.2")
    implementation(kotlin("stdlib-jdk8"))
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

gradlePlugin {
    plugins {
        create("androidBuildPlugin") {
            id = "org.ak2.android.build"
            implementationClass = "org.ak2.android.build.BuildPlugin"
        }
    }
}

pluginBundle {
    // These settings are set for the whole plugin bundle
    website = "https://github.com/a--v--k/android.build"
    vcsUrl = "https://github.com/a--v--k/android.build"

    // tags and description can be set for the whole bundle here, but can also
    // be set / overridden in the config for specific plugins
    description = "Android build extension plugin"

    (plugins) {

        // first plugin
        "androidBuildPlugin" {
            // id is captured from java-gradle-plugin configuration
            displayName = "Android build extension plugin"
            tags = listOf("android", "ak2")
            version = project.version.toString()
        }
    }
}
