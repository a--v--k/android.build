package org.ak2.android.build.signing

import com.android.build.gradle.AppExtension
import org.ak2.android.build.configurators.androidProject
import org.ak2.android.build.extras.doOnce
import java.io.File

open class BaseSigningConfiguratorKt(val buildType: String, val configName: String = "${buildType}SigningConfig", var params: SigningConfigParams? = null) {

    fun defineSigningConfig(android: AppExtension) {
        defineSigningConfig(android) { resolvedKeystoreFile, params ->
            buildTypes.maybeCreate(buildType).signingConfig = signingConfigs.maybeCreate(configName).apply {
                storeFile = resolvedKeystoreFile
                storePassword = params.storePassword
                keyAlias = params.keyAlias
                keyPassword = params.keyPassword
                println("${android.androidProject.path}: signingConfig created - $configName:$resolvedKeystoreFile")
            }

        }
    }

    protected fun defineSigningConfig(android: AppExtension, block: AppExtension.(File, SigningConfigParams) -> Unit) {
        val actualParams = params
        if (actualParams == null) {
            println("${android.androidProject.path}: keystore file not defined for $buildType")
            return
        }

        val fileName = actualParams.storeFile
        val file = android.androidProject.rootProject.file(fileName)
        require(file.exists()) { "Keystore file not found: $fileName" }

        doOnce(android, configName) {
            android.block(file, actualParams)
        }
    }
}