package org.ak2.android.build.tests

import org.junit.Assert
import org.junit.Test
import java.io.File
import java.util.*

class SimpleAppTest : BaseTest("002-simple-app") {


    protected override fun onSetup() {
        super.onSetup();
        val cert = "test-app-certificate.jks";
        Assert.assertTrue(resources.child(cert).copyTo(target.child(cert), overwrite = true).exists());
    }

    @Test
    fun `assemble`() {
        run("clean", "assemble") {

            val apkOutputFolder = buildFolder.child("outputs/packages")

            Assert.assertTrue(
                "Debug APK not found",
                File(apkOutputFolder, "debug/002-simple-app/simple-app-debug.apk").isFile
            )
            Assert.assertTrue(
                "Release APK not found",
                File(apkOutputFolder, "release/002-simple-app/1.0.0/simple-app-1.0.0-100000.apk").isFile
            )
        }
    }
}

