package org.ak2.android.build.signing

import com.android.build.gradle.AppExtension
import com.android.build.gradle.internal.dsl.ProductFlavor
import org.ak2.android.build.configurators.androidProject

class ReleaseAppFlavorSigningConfiguratorKt(appName: String, params: SigningConfigParams? = null) : BaseSigningConfiguratorKt("release", "${appName}ReleaseSigningConfig", params) {

    fun defineSigningConfig(android: AppExtension, appFlavor: ProductFlavor) {
        defineSigningConfig(android) { resolvedKeystoreFile, params ->
            appFlavor.signingConfig = signingConfigs.maybeCreate(configName).apply {
                storeFile = resolvedKeystoreFile
                storePassword = params.storePassword
                keyAlias = params.keyAlias
                keyPassword = params.keyPassword
                println("${android.androidProject.path}: signingConfig created - $configName:$resolvedKeystoreFile")
            }
        }
    }
}
