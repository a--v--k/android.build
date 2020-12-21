plugins {
    id("org.ak2.android.build")
}

repositories {
    google()
    jcenter()
    mavenCentral()
}

androidLibrary {

    dependsOn {
        compileOnly += library("androidx.annotation:annotation:1.1.0")
        compileOnly += library("org.jetbrains:annotations:13.0")
    }
}
