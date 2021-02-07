package org.ak2.android.build.tests

import org.junit.Assert
import org.junit.Test

class AppSetTest : BaseTest("003-app-set") {

    override fun onSetup() {
        super.onSetup();
        val cert = "test-app-certificate.jks";
        Assert.assertTrue(resources.child(cert).copyTo(target.child(cert), overwrite = true).exists());
    }

    @Test
    fun distribute() {
        run("clean") {
            Assert.assertFalse(buildFolder.exists())
        }

        run("distribute") {

            val apkOutputFolder = buildFolder.child("outputs/apk")

            for(i in 1..3) {
                assertFileNotExist("Debug APK found, but not expected", apkOutputFolder, "app${i}/debug/003-app-set-app${i}-debug.apk")
                   assertFileExist("Release APK not found",             apkOutputFolder, "app${i}/release/003-app-set-app${i}-release.apk")
            }

            val distFolder = buildFolder.child("outputs/dist")

            for(i in 1..3) {
                assertFileExist("Release      APK not found", distFolder, "app-set-app${i}/${i}.0.0/app-set-app${i}-${i}.0.0-${i}00000.apk")
                assertFileExist("Proguard mapping not found", distFolder, "app-set-app${i}/${i}.0.0/mapping/app-set-app${i}-${i}.0.0-${i}00000.mapping.txt")
            }
        }

        run("assemble") {

            val apkOutputFolder = buildFolder.child("outputs/apk")

            for(i in 1..3) {
                assertFileExist("Debug APK not found",apkOutputFolder, "app${i}/debug/003-app-set-app${i}-debug.apk")
            }
        }
    }

}

