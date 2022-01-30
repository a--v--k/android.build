import org.ak2.android.build.flavors.AndroidPlatforms.*
import org.ak2.android.build.ndk.NativeOptions
import org.ak2.android.build.ndk.NdkOptions
import org.ak2.android.build.release.createZip

plugins {
    id("org.ak2.android.build")
}

androidApp {

    debug {
        buildFor(android41x) { native(actual(native32())) }
        buildFor(android5xx) { native(actual(native64())) }
    }

    release {
        buildFor(android41x) { native(actual(native32())) }
        buildFor(android5xx) { native(actual(native64())) }
    }

    additionalManifests = fileTree("src/main/manifests").apply { include("*/AndroidManifest.xml") }

    dependsOn {
        compileOnly += library("androidx.annotation:annotation:1.1.0")
        compileOnly += library("org.jetbrains:annotations:13.0")

        implementation += module(":samples:004-multi-module-project:java-library")
        implementation += prefab(":samples:004-multi-module-project:standalone-log")
        implementation += prefab(":samples:004-multi-module-project:standalone-jni")
    }

    nativeOptions {

        apply(NdkOptions(other = NativeOptions().apply {
            c_flags(
                "-fvisibility=hidden"
            )
            cpp_flags(
                "-fvisibility=hidden"
            )
        }))

        libraries("native-lib")
    }

    release {
        onRelease { appInfo ->
            createZip(appInfo, "src", "sources") {
                from(file("src")) {
                    include("main/")
                    into("src")
                }
            }
        }
    }
}
