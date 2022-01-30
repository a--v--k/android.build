import org.ak2.android.build.flavors.AndroidPlatforms.*
import org.ak2.android.build.ndk.NativeOptions
import org.ak2.android.build.ndk.NdkOptions

androidLibrary {

    buildFor(android41x) { native(actual(native32())) }
    buildFor(android5xx) { native(actual(native64())) }

    dependsOn {
        api +=  prefab(":samples:004-multi-module-project:standalone-log")
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

        prefab("standalone-jni", "src/main/jni/include")
    }
}
