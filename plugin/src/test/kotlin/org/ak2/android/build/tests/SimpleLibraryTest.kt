package org.ak2.android.build.tests

import org.junit.Assert
import org.junit.Test
import java.io.File

class SimpleLibraryTest : BaseTest("001-simple-library") {

    override fun onSetup() {
        Assert.assertTrue(testSettingsSource.copyTo(testSettingsTarget, overwrite = true).exists());
    }

    @Test
    fun `check test setup`() {
        run("tasks")
    }

    @Test
    fun `build`() {
        run("clean", "build") {

            val aarOutputFolder = testProjectBuildFolder.child("outputs/aar")

            Assert.assertTrue(
                "Debug AAR not found",
                File(aarOutputFolder, "001-simple-library-debug.aar").isFile
            )
            Assert.assertTrue(
                "Release AAR not found",
                File(aarOutputFolder, "001-simple-library-release.aar").isFile
            )
        }
    }

    @Test
    fun `clean build and hardClean`() {

        run("clean", "build") {
            Assert.assertTrue("Build folder not exist", testProjectBuildFolder.exists())
        }


        run("hardClean") {
            Assert.assertTrue("Build folder exist", !testProjectBuildFolder.exists())
        }
    }

}

