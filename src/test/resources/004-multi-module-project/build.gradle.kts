import org.ak2.android.build.AndroidVersion.ANDROID_11_0
import org.ak2.android.build.ndk.NdkVersion.R21

plugins {
    id("org.ak2.android.build")
}

androidRoot {

    with(config) {
        compileSdkVersion = ANDROID_11_0
        ndkVersion = R21
        distributionRootDir = file("releases")
    }

    addRepositories {
        google()
        mavenCentral()
    }
}
