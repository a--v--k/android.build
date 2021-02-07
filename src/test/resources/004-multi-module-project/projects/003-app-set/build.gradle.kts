import org.ak2.android.build.release.createZip

plugins {
    id("org.ak2.android.build")
}

repositories {
    google()
    jcenter()
    mavenCentral()
}

androidAppSet {

    dependsOn {
        compileOnly += library("androidx.annotation:annotation:1.1.0")
        compileOnly += library("org.jetbrains:annotations:13.0")

        implementation += module(":projects:001-simple-library")
    }

    app("app1", "org.ak2.android.build.tests.appSet.app1") {
        release {
            onRelease { appInfo ->
                createZip(appInfo, "src1", "sources") {
                    from(file("src")) {
                        include("main/", "app1/")
                        into("src")
                    }
                }
            }
        }
    }

    app("app2", "org.ak2.android.build.tests.appSet.app2") {
        release {
            onRelease { appInfo ->
                createZip(appInfo, "src2", "sources") {
                    from(file("src")) {
                        include("main/", "app2/")
                        into("src")
                    }
                }
            }
        }
    }

    app("app3", "org.ak2.android.build.tests.appSet.app3") {
        release {
            onRelease { appInfo ->
                createZip(appInfo, "src3", "sources") {
                    from(file("src")) {
                        include("main/", "app3/")
                        into("src")
                    }
                }
            }
        }
    }
}
