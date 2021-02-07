package org.ak2.android.build.tests

import org.junit.Assert
import org.junit.Test

class SimpleAppTest : BaseTest("002-simple-app") {

    override fun onSetup() {
        super.onSetup();
        val cert = "test-app-certificate.jks";
        Assert.assertTrue(
            resources.child(cert).copyTo(target.child(cert), overwrite = true).exists()
        );
    }

    @Test
    fun distribute() {
        run("clean") {
            Assert.assertFalse(buildFolder.exists())
        }

        run("distribute") {
            val apkOutputFolder = buildFolder.child("outputs/apk")

            assertFileNotExist("Debug APK found, but not expected", apkOutputFolder, "debug/002-simple-app-debug.apk")
               assertFileExist("Release APK not found",             apkOutputFolder, "release/002-simple-app-release.apk")

            val distFolder = buildFolder.child("outputs/dist")

            assertFileExist("Release      APK not found", distFolder, "simple-app/1.0.0/simple-app-1.0.0-100000.apk")
            assertFileExist("Proguard mapping not found", distFolder, "simple-app/1.0.0/mapping/simple-app-1.0.0-100000.mapping.txt")
        }

        run("assemble") {
            val apkOutputFolder = buildFolder.child("outputs/apk")

            assertFileExist("Debug APK not found", apkOutputFolder, "debug/002-simple-app-debug.apk")
        }
    }
}

