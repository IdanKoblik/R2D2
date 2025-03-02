package dev.idank.r2d2

import com.intellij.openapi.project.Project
import com.intellij.testFramework.LightProjectDescriptor
import com.intellij.testFramework.fixtures.*
import com.intellij.testFramework.fixtures.impl.LightTempDirTestFixtureImpl
import java.util.*

abstract class BaseTest {

    protected lateinit var project: Project
    protected lateinit var myFixture: CodeInsightTestFixture

    @Throws(Exception::class)
    protected open fun setUp() {
        myFixture = createMyFixture()

        myFixture.testDataPath = "src/test/testData"

        if (!javaClass.isAnnotationPresent(AnnotatorTest::class.java)) myFixture.configureByText(
            javaClass.name + UUID.randomUUID(),
            "test"
        )

        myFixture.setUp()

        this.project = myFixture.project
    }

    @Throws(Exception::class)
    protected open fun tearDown() {
        myFixture.tearDown()
    }

    private fun createMyFixture(): CodeInsightTestFixture {
        val factory = IdeaTestFixtureFactory.getFixtureFactory()
        val fixtureBuilder = factory.createLightFixtureBuilder(getProjectDescriptor(), "test")
        val fixture = fixtureBuilder.fixture

        return IdeaTestFixtureFactory.getFixtureFactory().createCodeInsightFixture(fixture, createTempDirTestFixture())
    }

    private fun getProjectDescriptor(): LightProjectDescriptor? {
        return null
    }

    private fun createTempDirTestFixture(): TempDirTestFixture {
        val policy = IdeaTestExecutionPolicy.current()
        return if (policy != null)
            policy.createTempDirTestFixture()
        else
            LightTempDirTestFixtureImpl(true)
    }
}