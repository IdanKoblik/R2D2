package dev.idank.r2d2;

import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.fixtures.*;
import com.intellij.testFramework.fixtures.impl.LightTempDirTestFixtureImpl;
import org.jetbrains.plugins.github.api.GithubServerPath;
import org.jetbrains.plugins.github.authentication.accounts.GithubAccount;
import org.jetbrains.plugins.gitlab.api.GitLabServerPath;
import org.jetbrains.plugins.gitlab.authentication.accounts.GitLabAccount;

import java.util.UUID;

public abstract class BaseTest {

    protected CodeInsightTestFixture myFixture;

    protected GithubAccount defaultGithubAccount = new GithubAccount(
            "tester-GH",
            new GithubServerPath("github.com"),
            "adf5220b-407f-48e6-8652-b10b1d105313"
    );

    protected GitLabAccount defaultGitlabAccount = new GitLabAccount(
            "adf5220b-407f-48e6-8652-b10b1d105313",
            "tester-GL",
            new GitLabServerPath("https://gitlab.com")
    );


    protected void setUp() throws Exception {
        myFixture = createMyFixture();

        myFixture.setTestDataPath("src/test/testData");
        myFixture.setUp();

        if (!getClass().isAnnotationPresent(AnnotatorTest.class))
            myFixture.configureByText(getClass().getName() + UUID.randomUUID(), "test");
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
