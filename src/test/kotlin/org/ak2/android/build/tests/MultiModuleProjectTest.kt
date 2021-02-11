package org.ak2.android.build.tests

import org.junit.Assert
import org.junit.Test

class MultiModuleProjectTest : BaseTest("004-multi-module-project") {

    override fun onSetup() {
        super.onSetup();
        val cert = "test-app-certificate.jks";
        Assert.assertTrue(resources.child(cert).copyTo(target.child(cert), overwrite = true).exists());
    }

    @Test
    fun distribute() {
        run("clean", "distribute") {

            val distFolder = target.child("releases")

            assertFileExist("Release APK      not found", distFolder, "simple-app/1.0.0/simple-app-1.0.0-100000.apk")
            assertFileExist("Proguard mapping not found", distFolder, "simple-app/1.0.0/mapping/simple-app-1.0.0-100000.mapping.txt")
            assertFileExist("Source   ZIP     not found", distFolder, "simple-app/1.0.0/sources/simple-app-src-1.0.0.zip")

            for(i in 1..3) {
                assertFileExist("Release  APK     not found", distFolder, "app-set-app${i}/${i}.0.0/app-set-app${i}-${i}.0.0-${i}00000.apk")
                assertFileExist("Proguard mapping not found", distFolder, "app-set-app${i}/${i}.0.0/mapping/app-set-app${i}-${i}.0.0-${i}00000.mapping.txt")
                assertFileExist("Source   ZIP     not found", distFolder, "app-set-app${i}/${i}.0.0/sources/app-set-app${i}-src${i}-${i}.0.0.zip")
            }
        }
    }
}

