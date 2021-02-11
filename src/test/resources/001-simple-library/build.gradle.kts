plugins {
    id("org.ak2.android.build")
}

androidLibrary {

    dependsOn {
        compileOnly += library("androidx.annotation:annotation:1.1.0")
        compileOnly += library("org.jetbrains:annotations:13.0")
    }
}
