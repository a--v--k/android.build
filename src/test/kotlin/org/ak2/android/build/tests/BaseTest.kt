package org.ak2.android.build.tests

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.util.*

open class BaseTest(val projectRootDir: String) {

    @get:Rule
    var testProjectDir = TemporaryFolder()

    protected val resources = File("src/test/resources")
    protected lateinit var source: File
    protected lateinit var target: File
    protected lateinit var buildFolder: File

    protected lateinit var gradleRunner: GradleRunner

    @Before
    fun setup() {
        val sdkDir = sdkDir()

        source = File(resources, projectRootDir)
        Assert.assertTrue("Not a directory: ${source.absolutePath}", source.isDirectory)

        target = File(testProjectDir.root, source.name)
        source.copyRecursively(target = target, overwrite = true)
        Assert.assertTrue("Not a directory: ${target.absolutePath}", target.isDirectory)

        FileWriter(File(target, "local.properties")).use {
            it.write("sdk.dir=${sdkDir}")
        }

        buildFolder = File(target, "build")

        // creates and configures gradle runner                         <-------- (3)
        gradleRunner = GradleRunner.create()
            .withDebug(true)
            .withPluginClasspath()
            .withProjectDir(target)
            .withTestKitDir(testProjectDir.newFolder())
    }

    protected fun run(vararg tasks: String, checkAction: (BuildResult) -> Unit = {}) {
        val result = gradleRunner
            .forwardOutput()
            .withArguments(*tasks)
            .build()

        println(result.tasks)

        checkAction(result)
    }

    private fun sdkDir(): String {
        val pluginRoot = File(".")
        val localProperties = File(pluginRoot, "local.properties")
        Assert.assertTrue("local.properties not exist", localProperties.exists())

        val localPropertyValues = Properties().apply {
            FileReader(localProperties).use {
                load(it)
            }
        }

        val sdkDir = localPropertyValues.getProperty("sdk.dir", "")
        Assert.assertTrue("sdk.dir property missed", sdkDir.isNotEmpty())
        Assert.assertTrue("sdk.dir pointed to wrong location", File(sdkDir).isDirectory)

        return sdkDir
    }

    protected fun File.child(childFileName: String) = File(this, childFileName)

}