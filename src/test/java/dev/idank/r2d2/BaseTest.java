package dev.idank.r2d2;

import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.fixtures.*;
import com.intellij.testFramework.fixtures.impl.LightTempDirTestFixtureImpl;

public abstract class BaseTest {

    protected CodeInsightTestFixture myFixture;

    protected void setUp() throws Exception {
        myFixture = createMyFixture();

        myFixture.setTestDataPath("src/test/testData");
        myFixture.setUp();
    }

    protected void tearDown() throws Exception {
        myFixture.tearDown();
        myFixture = null;
    }

    protected CodeInsightTestFixture createMyFixture() {
        IdeaTestFixtureFactory factory = IdeaTestFixtureFactory.getFixtureFactory();
        TestFixtureBuilder<IdeaProjectTestFixture> fixtureBuilder = factory.createLightFixtureBuilder(getProjectDescriptor(), "test");
        IdeaProjectTestFixture fixture = fixtureBuilder.getFixture();

        return IdeaTestFixtureFactory.getFixtureFactory().createCodeInsightFixture(fixture, createTempDirTestFixture());
    }

    protected LightProjectDescriptor getProjectDescriptor() {
        return null;
    }

    protected TempDirTestFixture createTempDirTestFixture() {
        IdeaTestExecutionPolicy policy = IdeaTestExecutionPolicy.current();
        return policy != null
                ? policy.createTempDirTestFixture()
                : new LightTempDirTestFixtureImpl(true);
    }

}
