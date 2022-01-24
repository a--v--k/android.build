package org.ak2.android.build.tests

import org.gradle.api.JavaVersion
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

val gradleCmdArgs = arrayOf(
    "--info",
    "--stacktrace",
    "-Djavax.xml.parsers.SAXParserFactory=com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl"
)

abstract class BaseTest(val projectNamer: String) {

    protected val testProjectsSource = File("../samples").canonicalFile

    protected val testKit = File("~/.gradle").canonicalFile
    protected val testProjectsTarget = File("build/tests").canonicalFile

    protected val testProjectSource = File(testProjectsSource, projectNamer).canonicalFile
    protected val testProjectTarget = File(testProjectsTarget, projectNamer).canonicalFile

    protected val testProjectBuildFolder = File(testProjectTarget, "build").canonicalFile

    protected val testCertificateName = "test-app-certificate.jks"
    protected val testCertificateSource = File("../",  testCertificateName)
    protected val testCertificateTarget = File(testProjectTarget,  testCertificateName)

    protected val testSettingsName = "settings.gradle.kts"
    protected val testSettingsSource= File("src/test/resources",  testSettingsName)
    protected val testSettingsTarget = File(testProjectTarget,  testSettingsName)

    protected lateinit var gradleRunner: GradleRunner

    @Before
    fun setup() {
        val current = JavaVersion.current()
        val minRequired = JavaVersion.VERSION_11
        require(current.isCompatibleWith(minRequired)) { "Java ${minRequired} expected but found: ${current}" }

        val sdkDir = sdkDir()

        // Creates folder for test projects
        testProjectsTarget.apply { mkdirs(); }; //testProjectDir.root

        Assert.assertTrue(
            "Not a directory: ${testProjectSource.absolutePath}",
            testProjectSource.isDirectory
        )

        testProjectSource.copyRecursively(target = testProjectTarget, overwrite = true)
        Assert.assertTrue(
            "Not a directory: ${testProjectTarget.absolutePath}",
            testProjectTarget.isDirectory
        )

        FileWriter(File(testProjectTarget, "local.properties")).use {
            it.write("sdk.dir=${sdkDir}")
        }

        onSetup();

        val gradleVersion = gradleVersion()

        // creates and configures gradle runner                         <-------- (3)
        gradleRunner = GradleRunner.create()
            .withDebug(true)
            .withPluginClasspath()
            .withProjectDir(testProjectTarget)
            .withGradleVersion(gradleVersion)
            .withTestKitDir(testKit)
    }

    protected abstract fun onSetup();

    protected fun run(vararg tasks: String, checkAction: (BuildResult) -> Unit = {}) {
        val result = gradleRunner
            .forwardOutput()
            .withArguments(*tasks, *gradleCmdArgs)
            .build()

        println(result.tasks)

        checkAction(result)
    }

    private fun gradleVersion() : String {
        return "7.3.3"
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

    protected fun assertFileExist(msg: String, dir: File, fileName: String) {
        assertFileExist(msg, File(dir, fileName))
    }

    protected fun assertFileExist(msg: String, file: File) {
        Assert.assertTrue(msg + ": " + file.absoluteFile, file.isFile)
    }

    protected fun assertFileNotExist(msg: String, dir: File, fileName: String) {
        assertFileNotExist(msg, File(dir, fileName))
    }

    protected fun assertFileNotExist(msg: String, file: File) {
        Assert.assertFalse(msg + ": " + file.absoluteFile, file.exists())
    }

}