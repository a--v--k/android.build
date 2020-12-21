package org.ak2.android.build.tests

import org.junit.Assert
import org.junit.Test
import java.io.File
import java.util.*

class SimpleLibraryTest : BaseTest("001-simple-library") {

    @Test
    fun `check test setup`() {
        run("tasks")
    }

    @Test
    fun `build`() {
        run("clean", "build") {

            val aarOutputFolder = buildFolder.child("outputs/aar")

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
            Assert.assertTrue("Build folder not exist", buildFolder.exists())
        }


        run("hardClean") {
            Assert.assertTrue("Build folder exist", !buildFolder.exists())
        }
    }

}

