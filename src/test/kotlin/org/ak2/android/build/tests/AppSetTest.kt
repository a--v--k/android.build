package org.ak2.android.build.tests

import org.junit.Assert
import org.junit.Test
import java.io.File
import java.util.*

class AppSetTest : BaseTest("003-app-set") {


    protected override fun onSetup() {
        super.onSetup();
        val cert = "test-app-certificate.jks";
        Assert.assertTrue(resources.child(cert).copyTo(target.child(cert), overwrite = true).exists());
    }

    @Test
    fun `assemble`() {
        run("clean", "assemble") {

            val apkOutputFolder = buildFolder.child("outputs/packages")

            for(i in 1..3) {
                Assert.assertTrue(
                    "Debug APK not found",
                    File(apkOutputFolder, "debug/app${i}/app-set-app${i}-debug.apk").isFile
                )
                Assert.assertTrue(
                    "Release APK not found",
                    File(apkOutputFolder, "release/app${i}/${i}.0.0/app-set-app${i}-${i}.0.0-${i}00000.apk").isFile
                )
            }
        }
    }
}

