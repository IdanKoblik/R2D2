/*
 * MIT License
 *
 * Copyright (c) 2025 Idan Koblik
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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