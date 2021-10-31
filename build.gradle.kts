import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.konan.properties.loadProperties

plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    id("com.gradle.plugin-publish")
}

val ak2AndroidBuildPluginVersion    : String by project
val kotlinVersion                   : String by project
val androidBuildPluginVersion       : String by project

val jacocoVersion : String by project
val languageToolVersion : String by project
val junitVersion : String by project

group = "org.ak2"
version = ak2AndroidBuildPluginVersion

require(File("local.properties").exists()) { """
The following properties must be stored in local.properties:
gradle.publish.key
gradle.publish.secret
""".trimIndent() }

loadProperties("local.properties").forEach { key, value -> project.ext.set(key.toString(), value) }

dependencies {
    implementation(gradleApi())
    implementation("com.android.tools.build:gradle:$androidBuildPluginVersion")
    implementation("org.jacoco:org.jacoco.core:$jacocoVersion")
    implementation("org.languagetool:languagetool-core:$languageToolVersion")
    implementation(kotlin("reflect", kotlinVersion))

    testImplementation(gradleTestKit())
    testImplementation("junit:junit:$junitVersion")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

tasks.test {
    useJUnit()
    testLogging {
        events("failed", "skipped", "failed", "standard_out", "standard_error")
    }
}

gradlePlugin {
    plugins {
        create("androidBuildPlugin") {
            id = "org.ak2.android.build"
            implementationClass = "org.ak2.android.build.BuildPlugin"
        }
    }

    testSourceSets(sourceSets.test.get())
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
