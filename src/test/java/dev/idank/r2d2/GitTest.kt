package dev.idank.r2d2

import com.intellij.openapi.GitRepositoryInitializer
import com.intellij.openapi.project.Project
import com.intellij.testFramework.PlatformTestUtil
import com.intellij.testFramework.fixtures.CodeInsightTestFixture
import com.intellij.testFramework.fixtures.IdeaProjectTestFixture
import com.intellij.testFramework.fixtures.IdeaTestFixtureFactory
import com.intellij.testFramework.fixtures.TestFixtureBuilder
import dev.idank.r2d2.git.Platform
import git4idea.GitVcs
import git4idea.repo.GitRepositoryManager
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockWebServer
import org.jetbrains.plugins.github.api.GithubServerPath
import org.jetbrains.plugins.github.authentication.accounts.GHAccountManager
import org.jetbrains.plugins.github.authentication.accounts.GithubAccount
import org.jetbrains.plugins.gitlab.api.GitLabServerPath
import org.jetbrains.plugins.gitlab.authentication.accounts.GitLabAccount
import org.jetbrains.plugins.gitlab.authentication.accounts.PersistentGitLabAccountManager
import kotlin.io.path.Path

open class GitTest {

    protected val defaultGithubAccount: GithubAccount = GithubAccount(
        System.getProperty("github.user"),
        GithubServerPath("github.com"),
        System.getProperty("github.token")
    )

    protected val defaultGitlabAccount: GitLabAccount = GitLabAccount(
        System.getProperty("gitlab.token"),
        System.getProperty("gitlab.user"),
        GitLabServerPath("https://gitlab.com")
    )

    protected lateinit var myFixture: CodeInsightTestFixture
    protected lateinit var project: Project
    protected var platform: Platform = Platform.GITHUB
    protected var mockWebServer: MockWebServer? = null

    protected open fun setUp() {
        this.myFixture = createMyFixture()

        myFixture.setUp()
        this.project = myFixture.project

        val gitlabAccountManager = PersistentGitLabAccountManager()
        val githubAccountManager = GHAccountManager()

        runBlocking {
            githubAccountManager.updateAccount(defaultGithubAccount, defaultGithubAccount.id)
            gitlabAccountManager.updateAccount(defaultGitlabAccount, defaultGitlabAccount.id)
        }

        GitVcs.getInstance(myFixture.project).doActivate()
        val projectRoot = PlatformTestUtil.getOrCreateProjectBaseDir(myFixture.project)
        GitRepositoryInitializer.getInstance()?.initRepository(myFixture.project, projectRoot)
        println(GitRepositoryManager.getInstance(project).repositories)

        this.mockWebServer = MockWebServer()
    }

    protected open fun tearDown() {
        mockWebServer?.shutdown()
        myFixture.tearDown()
    }


    private fun createMyFixture(): CodeInsightTestFixture {
        val fixtureBuilder = constructFixtureBuilder()
        val fixture = fixtureBuilder.fixture

        return IdeaTestFixtureFactory.getFixtureFactory().createCodeInsightFixture(fixture)
    }

    private fun constructFixtureBuilder(): TestFixtureBuilder<IdeaProjectTestFixture> {
        val factory = IdeaTestFixtureFactory.getFixtureFactory()
        return factory.createFixtureBuilder(platform.getName(), Path("/tmp/repo/"), true)
    }
}