plugins {
    id("org.ak2.android.build")
}

androidAppSet {

    dependsOn {
        compileOnly += library("androidx.annotation:annotation:1.1.0")
        compileOnly += library("org.jetbrains:annotations:13.0")
    }

    app("app1", "org.ak2.android.build.tests.appSet.app1" ) {
    }

    app("app2", "org.ak2.android.build.tests.appSet.app2") {
    }

    app("app3", "org.ak2.android.build.tests.appSet.app3") {
    }
}
