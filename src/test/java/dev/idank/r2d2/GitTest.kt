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
        System.getProperty("github.user") ?: System.getProperty("GH_USER"),
        GithubServerPath("github.com"),
        System.getProperty("github.token") ?: System.getenv("GH_TOKEN")
    )

    protected val defaultGitlabAccount: GitLabAccount = GitLabAccount(
        System.getProperty("gitlab.token") ?: System.getProperty("GL_TOKEN"),
        System.getProperty("gitlab.user") ?: System.getProperty("GL_USER"),
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

        PluginUtils.updateGitAccount(gitLabAccountManager, defaultGitlabAccount, PluginUtils.Action.INSERT)
        PluginUtils.updateGitAccount(githubAccountManager, defaultGithubAccount, PluginUtils.Action.INSERT)

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