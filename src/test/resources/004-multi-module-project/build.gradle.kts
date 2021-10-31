import org.ak2.android.build.AndroidVersion.*
import org.ak2.android.build.ndk.NdkVersion.*

plugins {
    id("org.ak2.android.build")
}

androidRoot {

    with(config) {
        compileSdkVersion = ANDROID_12_0
        ndkVersion = R22
        distributionRootDir = file("releases")
    }

    addRepositories {
        google()
        mavenCentral()
    }
}
