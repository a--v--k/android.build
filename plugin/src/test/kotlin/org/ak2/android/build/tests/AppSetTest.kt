package org.ak2.android.build.tests

import org.junit.Assert
import org.junit.Test

class AppSetTest : BaseTest("003-app-set") {

    override fun onSetup() {
        Assert.assertTrue(testSettingsSource.copyTo(testSettingsTarget, overwrite = true).exists());
        Assert.assertTrue(testCertificateSource.copyTo(testCertificateTarget, overwrite = true).exists());
    }

    @Test
    fun distribute() {
        run("clean") {
            Assert.assertFalse(testProjectBuildFolder.exists())
        }

        run("distribute") {

            val apkOutputFolder = testProjectBuildFolder.child("outputs/apk")

            for(i in 1..3) {
                assertFileNotExist("Debug APK found, but not expected", apkOutputFolder, "app${i}/debug/003-app-set-app${i}-debug.apk")
                   assertFileExist("Release APK not found",             apkOutputFolder, "app${i}/release/003-app-set-app${i}-release.apk")
            }

            val distFolder = testProjectBuildFolder.child("outputs/dist")

            for(i in 1..3) {
                assertFileExist("Release      APK not found", distFolder, "app-set-app${i}/${i}.0.0/app-set-app${i}-${i}.0.0-${i}00000.apk")
                assertFileExist("Proguard mapping not found", distFolder, "app-set-app${i}/${i}.0.0/mapping/app-set-app${i}-${i}.0.0-${i}00000.mapping.txt")
            }
        }

        run("assemble") {

            val apkOutputFolder = testProjectBuildFolder.child("outputs/apk")

            for(i in 1..3) {
                assertFileExist("Debug APK not found",apkOutputFolder, "app${i}/debug/003-app-set-app${i}-debug.apk")
            }
        }
    }

}

