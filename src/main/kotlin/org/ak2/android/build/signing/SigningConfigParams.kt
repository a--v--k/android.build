package org.ak2.android.build.signing

import java.io.File
import java.io.FileReader
import java.util.*
import kotlin.collections.HashMap

data class SigningConfigParams(val storeFile: String, val storePassword: String, val keyAlias: String, val keyPassword: String) {

    companion object {

        data class ConfigCacheKey(val appFolder : File, val buildType: String)

        val cachedProperties = HashMap<File, Properties>()
        val cachedConfigs = HashMap<ConfigCacheKey, SigningConfigParams?>()

        fun fromProperties(buildType: String, appFolder: File): SigningConfigParams? {
            val key = ConfigCacheKey(appFolder, buildType)

            if (cachedConfigs.containsKey(key)) {
                return cachedConfigs[key]
            }

            val p = loadProperties(appFolder, "build.properties", "local.properties")

            val config = fromProperties(buildType, p)
            if (config != null) {
                println("!!! Property files has $buildType signing config: ${appFolder.absolutePath}")
            } else {
                println("!!! Property file has no $buildType signing config: ${appFolder.absolutePath}")
            }

            cachedConfigs[key] = config

            return config
        }

        private fun loadProperties(appFolder: File, vararg fileNames: String): Properties {
            if (cachedProperties.containsKey(appFolder)) {
                return cachedProperties[appFolder] as Properties
            }

            val p = Properties()

            for (fileName in fileNames) {
                val file = File(appFolder, fileName)
                if (file.exists()) {
                    FileReader(file).apply {
                        use {
                            p.load(this)
                        }
                    }
                    println("!!! Property file found: ${file.absolutePath}")
                } else {
                    println("!!! Property file not found: ${file.absolutePath}")
                }
            }

            cachedProperties[appFolder] = p

            return p
        }

        private fun fromProperties(buildType: String, p: Properties): SigningConfigParams? {
            val keystoreFileKey = "$buildType.keystore.file"
            val keystorePasswordKey = "$buildType.keystore.password"
            val keyAliasKey = "$buildType.key.alias"
            val keyPasswordKey = "$buildType.key.password"

            if (p.containsKey(keystoreFileKey)) {
                val storeFile = p.getProperty(keystoreFileKey)
                val storePassword = p.getProperty(keystorePasswordKey)
                val keyAlias = p.getProperty(keyAliasKey)
                val keyPassword = p.getProperty(keyPasswordKey)

                require(!storeFile.isNullOrEmpty()) { "$keystoreFileKey missed" }
                require(!storePassword.isNullOrEmpty()) { "$storePassword missed" }
                require(!keyAlias.isNullOrEmpty()) { "$keyAlias missed" }
                require(!keyPassword.isNullOrEmpty()) { "$keystoreFileKey missed" }

                return SigningConfigParams(storeFile, storePassword, keyAlias, keyPassword)
            }

            return null
        }
    }
}


