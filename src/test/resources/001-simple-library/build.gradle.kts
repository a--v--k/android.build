import org.ak2.android.build.dependencies.base.DependencyScope

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
        add("compileOnly", "androidx.annotation:annotation:1.1.0")
        add(DependencyScope.COMPILE_ONLY, library("org.jetbrains:annotations:13.0"))
    }
}
