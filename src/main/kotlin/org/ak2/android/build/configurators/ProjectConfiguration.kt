package org.ak2.android.build.configurators

import org.ak2.android.build.AndroidVersion.*
import org.ak2.android.build.signing.SigningConfigParams
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import java.io.File
import java.util.*


interface ProjectConfiguration {

    var javaVersion             : JavaVersion

    var kotlinVersion           : String
    var useKotlinInProd         : Boolean
    var useKotlinInTest         : Boolean

    var buildToolsVersion       : String
    var supportLibraryVersion   : String
    var constraintLayoutVersion : String

    var minSdkVersion           : org.ak2.android.build.AndroidVersion
    var compileSdkVersion       : org.ak2.android.build.AndroidVersion

    val debugSigningConfig      : SigningConfigParams?
    val releaseSigningConfig    : SigningConfigParams?

    var debugVersion            : AppVersionKt?
}

class RootConfiguration(val project: Project) : ProjectConfiguration {

    override var javaVersion             : JavaVersion   = JavaVersion.VERSION_1_8

    override var kotlinVersion           : String        = "1.3.41"
    override var useKotlinInProd         : Boolean       = false
    override var useKotlinInTest         : Boolean       = false

    override var buildToolsVersion       : String        = "28.0.3"
    override var supportLibraryVersion   : String        = "28.0.0"
    override var constraintLayoutVersion : String        = "1.1.3"

    override var minSdkVersion           : org.ak2.android.build.AndroidVersion = ANDROID_4_1
    override var compileSdkVersion       : org.ak2.android.build.AndroidVersion = ANDROID_9_0

    override val debugSigningConfig      : SigningConfigParams? = SigningConfigParams.fromProperties("debug", project.projectDir)
    override val releaseSigningConfig    : SigningConfigParams? = SigningConfigParams.fromProperties("release", project.projectDir)

    override var debugVersion            : AppVersionKt? = AppVersionKt(versionName="debug", majorVersionCode=999, minorVersionCode=999)
}

class InnerProjectConfiguration(val project: Project, val appFolder : File, val parentConfig: ProjectConfiguration) : ProjectConfiguration {

    private val properties = LinkedHashMap<String, Any?>()

    override var javaVersion             : JavaVersion
        get()      = getProperty("javaVersion") { javaVersion }
        set(value) = setProperty("javaVersion", value)

    override var kotlinVersion           : String
        get()      = getProperty("kotlinVersion") { kotlinVersion }
        set(value) = setProperty("kotlinVersion", value)

    override var useKotlinInProd         : Boolean
        get()      = getProperty("useKotlinInProd") { useKotlinInProd }
        set(value) = setProperty("useKotlinInProd", value)

    override var useKotlinInTest         : Boolean
        get()      = getProperty("useKotlinInTest") { useKotlinInTest }
        set(value) = setProperty("useKotlinInTest", value)

    override var buildToolsVersion       : String
        get()      = getProperty("buildToolsVersion") { buildToolsVersion }
        set(value) = setProperty("buildToolsVersion", value)

    override var supportLibraryVersion   : String
        get()      = getProperty("supportLibraryVersion") { supportLibraryVersion }
        set(value) = setProperty("supportLibraryVersion", value)

    override var constraintLayoutVersion : String
        get()      = getProperty("constraintLayoutVersion") { constraintLayoutVersion }
        set(value) = setProperty("constraintLayoutVersion", value)

    override var minSdkVersion           : org.ak2.android.build.AndroidVersion
        get()      = getProperty("minSdkVersion") { minSdkVersion }
        set(value) = setProperty("minSdkVersion", value)

    override var compileSdkVersion       : org.ak2.android.build.AndroidVersion
        get()      = getProperty("compileSdkVersion") { compileSdkVersion }
        set(value) = setProperty("compileSdkVersion", value)

    override val debugSigningConfig: SigningConfigParams?
        get()      = getSigningConfig("debug") { debugSigningConfig }

    override val releaseSigningConfig: SigningConfigParams?
        get()      = getSigningConfig("release") { releaseSigningConfig }

    override var debugVersion: AppVersionKt?
        get()      = getProperty("debugVersion") { debugVersion }
        set(value) = setProperty("debugVersion", value)



    private inline fun <reified T> getProperty(name: String, getter : ProjectConfiguration.() -> T): T {
        val result : T? = properties[name] as T?
        if (result != null) {
            return result
        }
        return parentConfig.getter()
    }

    private inline fun getSigningConfig(buildType: String, getter: ProjectConfiguration.() -> SigningConfigParams?): SigningConfigParams? {
        val name = "${buildType}SigningConfig"
        var result: SigningConfigParams? = properties[name] as SigningConfigParams?
        if (result != null) {
            return result
        }

        result = SigningConfigParams.fromProperties(buildType, appFolder)

        if (result != null) {
            properties[name] = result
            return result
        }

        return parentConfig.getter()
    }

    private inline fun <reified T> setProperty(name: String, value : T){
        properties[name] = value
    }
}

