## Android build extension plugin ##

This project provides simple Kotlin DSL working over Android Gradle Plugin.

The following features are supported:
- Build flavors for differrent application variants
- Build flavors for differrent Android API versions
- Build flavors for differrent Android native platforms (ndk-build only, cmake will be added later)
- Different build variants for release/debug versions
- Various project configuration parameters which may be inherited from parent project or defined separately
  - javaVersion
  - kotlinVersion
  - useKotlinInProd
  - useKotlinInTest
  - useViewBindings
  - buildToolsVersion
  - minSdkVersion
  - compileSdkVersion
- Various project build properties loaded from build.properties and local.properties which  which may be inherited from parent project or defined separately too
  - package.name
  - package.version.name
  - package.version.code.major
  - package.version.code.minor
  - release.keystore.file
  - release.keystore.password
  - release.key.alias
  - release.key.password
  - debug.keystore.file
  - debug.keystore.password
  - debug.key.alias
  - debug.key.password

Below you may find examples for various cases.

### Android library DSL ###

#### Simple library ####
> build.gradle.kts
```
androidLibrary {

    dependsOn {
        compileOnly += library("androidx.annotation:annotation:1.1.0")
        compileOnly += library("org.jetbrains:annotations:13.0")
    }
}
```

#### Simple library depends on another project module ####
> build.gradle.kts
```
androidLibrary {
    dependsOn {
        api += module(":projects:modules:utils")
    }
}
```
#### Library containing native code ####
> build.gradle.kts
```
import org.ak2.android.build.flavors.AndroidPlatforms.*

androidLibrary {

    dependsOn {
        api += modules(":projects:modules:utils")
        compileOnly += library("androidx.annotation:annotation:1.1.0")
        implementation += library("androidx.core:core:1.1.0")
        implementation += library("androidx.appcompat:appcompat:1.1.0")
    }

    buildFor(android41x) { native(actual(native32())) }
    buildFor(android5xx) { native(actual(native64())) }

    nativeOptions {
        args("NDK_TOOLCHAIN_VERSION=4.9", "APP_STL=gnustl_static", "APP_LDFLAGS+=-Wl,-s")

        c_flags("-O3", "-fvisibility=hidden")
        cpp_flags("-std=c++11", "-frtti", "-fexceptions", "-fvisibility=hidden", "-Wno-write-strings")

        libraries("mynative")
    }
}
```
### Android application DSL ###

#### Simple application ####
> build.gradle.kts
```
androidApp {

    config.minSdkVersion = ANDROID_8_0

    dependsOn {
        api += modules(":projects:modules:utils")
        compileOnly += library("androidx.annotation:annotation:1.1.0")
        implementation += library("androidx.core:core:1.1.0")
        implementation += library("androidx.appcompat:appcompat:1.1.0")
    }
}
```
Also the following files should be placed in the same folder with 
> build.properties
```
package.name=my-apk-name
package.version.name=3.2.1
package.version.code.major=321
package.version.code.minor=000
```
> local.properties - optional file overriding build.properties values, typically excluded from GIT or other VCS
```
release.keystore.file=<path-to-keystore-relative-to-root-project>
release.keystore.password=<key-store-password>

release.key.alias=<key-alias>
release.key.password=<key-password>

debug.keystore.file=<path-to-keystore-relative-to-root-project>
debug.keystore.password=<key-store-password>

debug.key.alias=<key-alias>
debug.key.password=<key-password>
```
#### Several applications with common code base ####
The layout for these applications is
```
app/
  - build.gradle.kts
  - local.properties
  - multidex.cfg
  + libs/
     + common/
     + app2/
  + src/
     + app1
        + assets/
        + java/
        + res/
        - AndroidManifest.xml
        - build.properties
        - proguard.cfg
     + app2
        + assets/
        + java/
        + res/
        - AndroidManifest.xml
        - build.properties
        - proguard.cfg
     + main
        + assets/
        + java/
        + jni/
        + res/
        - AndroidManifest.xml
```
> build.gradle.kts
```
import org.ak2.android.build.AndroidVersion.ANDROID_10_0
import org.ak2.android.build.configurators.config
import org.ak2.android.build.dependencies.ThirdParties
import org.ak2.android.build.flavors.AndroidPlatforms.*
import org.ak2.android.build.flavors.NativePlatforms.*
import org.ak2.android.build.release.createI18nArchives

/**
 * These optional properties are loaded from local.properties
 */
val app1Enabled: Boolean? by config.buildProperties
val app2Enabled: Boolean? by config.buildProperties

androidAppSet {

    config.compileSdkVersion = ANDROID_10_0

    dependsOn {
        compileOnly += library("androidx.annotation:annotation:1.1.0")
        compileOnly += library("org.jetbrains:annotations:13.0")

        implementation += library("androidx.viewpager2:viewpager2:1.0.0")
        implementation += library("androidx.documentfile:documentfile:1.0.1")

        implementation += library("com.google.android.material:material:1.1.0-beta01")
        implementation += library("androidx.constraintlayout:constraintlayout:2.0.0-beta4")
        implementation += library("androidx.multidex:multidex:2.0.1")

        implementation += local("libs/common/jcifs-1.3.18.jar")
        implementation += local("libs/common/JSnappy-0.9.1.jar")
    }

    before {
        defaultConfig.multiDexEnabled = true
        defaultConfig.multiDexKeepProguard = file("multidex.cfg")
    }

    app("app1", "org.my.app1", app1Enabled) {

        release {
            buildFor(android41x) { native(actual(native32())) }
            buildFor(android5xx) { native(actual(native64())) }

            onRelease { appName, appVersion ->
                createI18nArchives(project, appName, appVersion, languages)
            }

            proguard { shrinkResources = true }
        }

        debug {
            buildFor(android41x) { native(arm7) }
        }

        languages += setOf("en", "ru", "it", "tt", "pl", "de", "es", "sk", "ko", "bg")

        checkStrings {
            languagesToCheck += languages
        }
    }

    app("app2", "org.my.app2", app2Enabled) {

        release {
            buildFor(android41x) { native(actual(native32())) }
            buildFor(android5xx) { native(actual(native64())) }

            proguard { shrinkResources = true }
        }

        debug {
            buildFor(android41x) { native(arm7) }
        }

        dependsOn {
            implementation += local("libs/app2/libspen23.small.jar")
        }

        languages += setOf("en", "ru")

        checkStrings {
            languagesToCheck += languages
        }
    }

    nativeOptions {

        c_flags("-D__TEST__")

        args("NDK_TOOLCHAIN_VERSION=4.9", "APP_STL=gnustl_static", "APP_LDFLAGS+=-Wl,-s")

        c_flags("-DHAVE_CONFIG_H", "-DDEBUGLVL=0", "-D__ANDROID__", "-O3", "-fvisibility=hidden")

        cpp_flags("-std=c++11", "-frtti", "-fexceptions", "-fvisibility=hidden", "-Wno-write-strings")

        libraries("app")
    }
}
```
The src/app1 folder contains the following files
> build.properties
```
package.name=my-app1-apk-name
package.version.name=2.7.0-rc2
package.version.code.major=263
package.version.code.minor=110
```
> proguard.cfg
```
...
```
The src/app2 folder contains the following files
> build.properties
```
package.name=my-app2-apk-name
package.version.name=1.0.0
package.version.code.major=100
package.version.code.minor=000
```
> proguard.cfg
```
...
```
After assembling, APK files are moved into the following locations (it was true for 3.5.3 - 3.6.0, in 4.1.0 apks are placed in standard output location) :
```
app/
  + build/
      + outputs/
          + packages/
              + debug/
                  + app1/
                    - my-app1-apk-name-android41x-arm7-debug.apk
                  + app2/
                    - my-app2-apk-name-debug.apk
              + release
                  + app1/
                      + app1-version1/
                        - my-app1-apk-name-2.7.0-rc2-android41x-arm7-263110.apk
                        - my-app1-apk-name-2.7.0-rc2-android41x-x86-263111.apk
                        - my-app1-apk-name-2.7.0-rc2-android5xx-arm8-263112.apk
                        - my-app1-apk-name-2.7.0-rc2-android5xx-x64-263113.apk
                        + i18n/
                            - my-app1-apk-name-i18n-bg-2.7.0-rc2.zip
                            - my-app1-apk-name-i18n-de-2.7.0-rc2.zip
                            - my-app1-apk-name-i18n-en-2.7.0-rc2.zip
                            - my-app1-apk-name-i18n-es-2.7.0-rc2.zip
                            - my-app1-apk-name-i18n-it-2.7.0-rc2.zip
                            - my-app1-apk-name-i18n-ko-2.7.0-rc2.zip
                            - my-app1-apk-name-i18n-pl-2.7.0-rc2.zip
                            - my-app1-apk-name-i18n-ru-2.7.0-rc2.zip
                            - my-app1-apk-name-i18n-sk-2.7.0-rc2.zip
                  + app2/
                      + app2-version2/
                        - my-app2-apk-name-1.0.0-android41x-arm7-100000.apk
                        - my-app2-apk-name-1.0.0-android41x-x86-100001.apk
                        - my-app2-apk-name-1.0.0-android5xx-arm8-100002.apk
                        - my-app2-apk-name-1.0.0-android5xx-x64-100003.apk
```






