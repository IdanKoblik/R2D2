package dev.idank.r2d2

import com.intellij.openapi.GitRepositoryInitializer
import com.intellij.openapi.project.Project
import com.intellij.testFramework.PlatformTestUtil
import com.intellij.testFramework.fixtures.CodeInsightTestFixture
import com.intellij.testFramework.fixtures.IdeaProjectTestFixture
import com.intellij.testFramework.fixtures.IdeaTestFixtureFactory
import com.intellij.testFramework.fixtures.TestFixtureBuilder
import dev.idank.r2d2.git.Platform
import dev.idank.r2d2.services.PluginLoaderService
import dev.idank.r2d2.utils.PluginUtils
import git4idea.GitVcs
import org.jetbrains.plugins.github.api.GithubServerPath
import org.jetbrains.plugins.github.authentication.accounts.GHAccountManager
import org.jetbrains.plugins.github.authentication.accounts.GithubAccount
import org.jetbrains.plugins.gitlab.api.GitLabServerPath
import org.jetbrains.plugins.gitlab.authentication.accounts.GitLabAccount
import org.jetbrains.plugins.gitlab.authentication.accounts.PersistentGitLabAccountManager
import kotlin.io.path.Path

open class GitTest {

    protected lateinit var gitLabAccountManager: PersistentGitLabAccountManager
    protected lateinit var githubAccountManager: GHAccountManager

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
    protected lateinit var pluginLoader: PluginLoader

    protected var platform: Platform = Platform.GITHUB

    protected open fun setUp() {
        this.myFixture = createMyFixture()

        myFixture.setUp()
        this.project = myFixture.project

        this.gitLabAccountManager = PersistentGitLabAccountManager()
        this.githubAccountManager = GHAccountManager()

        PluginUtils.updateGitlabAccount(gitLabAccountManager, defaultGitlabAccount, PluginUtils.Action.INSERT)
        PluginUtils.updateGithubAccount(githubAccountManager, defaultGithubAccount, PluginUtils.Action.INSERT)

        GitVcs.getInstance(myFixture.project).doActivate()
        val projectRoot = PlatformTestUtil.getOrCreateProjectBaseDir(myFixture.project)
        GitRepositoryInitializer.getInstance()?.initRepository(myFixture.project, projectRoot)

        this.pluginLoader = project.getService(PluginLoaderService::class.java).pluginLoader
    }

    protected open fun tearDown() {
        myFixture.tearDown()
    }

    private fun createMyFixture(): CodeInsightTestFixture {
        val fixtureBuilder = constructFixtureBuilder()
        val fixture = fixtureBuilder.fixture

        return IdeaTestFixtureFactory.getFixtureFactory().createCodeInsightFixture(fixture)
    }

    private fun constructFixtureBuilder(): TestFixtureBuilder<IdeaProjectTestFixture> {
        val factory = IdeaTestFixtureFactory.getFixtureFactory()
        return factory.createFixtureBuilder(platform.test(), Path("/tmp/repo/"), true)
    }
}