package dev.idank.r2d2

import com.intellij.dvcs.repo.VcsRepositoryManager
import com.intellij.openapi.project.Project
import com.intellij.testFramework.LightPlatformTestCase.SimpleLightProjectDescriptor
import com.intellij.testFramework.LightProjectDescriptor
import com.intellij.testFramework.fixtures.*
import com.intellij.testFramework.fixtures.impl.LightTempDirTestFixtureImpl
import org.jetbrains.plugins.github.authentication.accounts.GHAccountManager
import org.jetbrains.plugins.gitlab.authentication.accounts.PersistentGitLabAccountManager
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*


val gitlabAccountManager = PersistentGitLabAccountManager()
val githubAccountManager = GHAccountManager()

open class BaseTest {

    protected var myFixture: CodeInsightTestFixture? = null

    @Throws(Exception::class)
    protected open fun setUp() {
        myFixture = createMyFixture()

        myFixture!!.testDataPath = "src/test/testData"
        myFixture!!.setUp()

        if (!javaClass.isAnnotationPresent(AnnotatorTest::class.java)) myFixture!!.configureByText(
            javaClass.name + UUID.randomUUID(),
            "test"
        )
    }

    @Throws(Exception::class)
    protected open fun tearDown() {
        myFixture!!.tearDown()
        myFixture = null
    }

    private fun createMyFixture(): CodeInsightTestFixture {
        val factory = IdeaTestFixtureFactory.getFixtureFactory()
        val fixtureBuilder = factory.createLightFixtureBuilder(getProjectDescriptor(), "test")
        val fixture = fixtureBuilder.fixture

        return IdeaTestFixtureFactory.getFixtureFactory().createCodeInsightFixture(fixture, createTempDirTestFixture())
    }

    private fun getProjectDescriptor(): LightProjectDescriptor? {
        return LightProjectDescriptor.EMPTY_PROJECT_DESCRIPTOR
    }

    private fun createTempDirTestFixture(): TempDirTestFixture {
        val policy = IdeaTestExecutionPolicy.current()
        return if (policy != null)
            policy.createTempDirTestFixture()
        else
            LightTempDirTestFixtureImpl(true)
    }



}