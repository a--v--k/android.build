/*
 * Copyright 2019 AK2.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ak2.android.build.configurators

import org.ak2.android.build.AndroidVersion.*
import org.ak2.android.build.properties.BuildProperties
import org.ak2.android.build.signing.ProguardConfig
import org.ak2.android.build.signing.SigningConfigParams
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import java.io.File
import java.util.*


interface ProjectConfiguration {

    val buildProperties         : BuildProperties

    var javaVersion             : JavaVersion

    var kotlinVersion           : String
    var useKotlinInProd         : Boolean
    var useKotlinInTest         : Boolean

    var useViewBindings         : Boolean

    var buildToolsVersion       : String
    var supportLibraryVersion   : String
    var constraintLayoutVersion : String

    var minSdkVersion           : org.ak2.android.build.AndroidVersion
    var compileSdkVersion       : org.ak2.android.build.AndroidVersion
    var targetSdkVersion        : org.ak2.android.build.AndroidVersion?

    val debugSigningConfig      : SigningConfigParams?
    val releaseSigningConfig    : SigningConfigParams?

    var proguardConfig          : ProguardConfig

    var debugVersion            : AppVersionKt?
}

val ProjectConfiguration.effectiveTargetSdkVersionCode : Int
    get() = targetSdkVersion?.code ?: Math.max(minSdkVersion.code, compileSdkVersion.code)


class RootConfiguration(val project: Project) : ProjectConfiguration {

    override val buildProperties         : BuildProperties = BuildProperties(project.projectDir)

    override var javaVersion             : JavaVersion   = JavaVersion.VERSION_1_8

    override var kotlinVersion           : String        = "1.4.0"
    override var useKotlinInProd         : Boolean       = false
    override var useKotlinInTest         : Boolean       = false

    override var useViewBindings         : Boolean       = false

    override var buildToolsVersion       : String        = "29.0.3"
    override var supportLibraryVersion   : String        = "28.0.0"
    override var constraintLayoutVersion : String        = "1.1.3"

    override var minSdkVersion           : org.ak2.android.build.AndroidVersion = ANDROID_4_1
    override var compileSdkVersion       : org.ak2.android.build.AndroidVersion = ANDROID_9_0
    override var targetSdkVersion        : org.ak2.android.build.AndroidVersion? = null

    override val debugSigningConfig      : SigningConfigParams? = loadSigningConfig("debug", buildProperties)
    override val releaseSigningConfig    : SigningConfigParams? = loadSigningConfig("release", buildProperties)

    override var proguardConfig          : ProguardConfig = ProguardConfig()

    override var debugVersion            : AppVersionKt? = AppVersionKt(versionName="debug", majorVersionCode=999, minorVersionCode=999)
}

class InnerProjectConfiguration(val project: Project, val appFolder : File, val parentConfig: ProjectConfiguration) : ProjectConfiguration {

    private val properties = LinkedHashMap<String, Any?>()

    override val buildProperties: BuildProperties = BuildProperties(appFolder, parentConfig.buildProperties)

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

    override var useViewBindings: Boolean
        get()      = getProperty("useViewBindings") { useViewBindings }
        set(value) = setProperty("useViewBindings", value)

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

    override var targetSdkVersion       : org.ak2.android.build.AndroidVersion?
        get()      = getProperty("targetSdkVersion") { targetSdkVersion }
        set(value) = setProperty("targetSdkVersion", value)

    override val debugSigningConfig: SigningConfigParams?
        get()      = getSigningConfig("debug") { debugSigningConfig }

    override val releaseSigningConfig: SigningConfigParams?
        get()      = getSigningConfig("release") { releaseSigningConfig }

    override var proguardConfig: ProguardConfig
        get()      = getProperty("proguardConfig") { proguardConfig }
        set(value) = setProperty("proguardConfig", value)

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

        result = loadSigningConfig(buildType, buildProperties)

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

private fun loadSigningConfig(buildType: String, p: BuildProperties): SigningConfigParams? {
    val keystoreFileKey = "$buildType.keystore.file"
    val keystorePasswordKey = "$buildType.keystore.password"
    val keyAliasKey = "$buildType.key.alias"
    val keyPasswordKey = "$buildType.key.password"

    val storeFile = p.opt<String>(keystoreFileKey)

    if (!storeFile.isNullOrEmpty()) {
        val storePassword = p.get<String>(keystorePasswordKey)
        val keyAlias = p.get<String>(keyAliasKey)
        val keyPassword = p.get<String>(keyPasswordKey)

        return SigningConfigParams(storeFile, storePassword, keyAlias, keyPassword)
    }

    return null
}

