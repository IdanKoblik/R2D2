package dev.idank.r2d2.git

import com.intellij.openapi.components.service
import dev.idank.r2d2.GitBaseTest
import dev.idank.r2d2.git.data.AuthData
import dev.idank.r2d2.git.data.GitProjectInfo
import dev.idank.r2d2.git.data.GitUser
import dev.idank.r2d2.git.request.GithubIssueRequest
import dev.idank.r2d2.git.request.GitlabIssueRequest
import dev.idank.r2d2.services.PluginLoaderService
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GitHostTest : GitBaseTest() {

    private var gitlabHttp: BaseHttpTest<GitlabIssueRequest, GitlabHost>? = null
    private var githubHttp: BaseHttpTest <GithubIssueRequest, GithubHost>? = null

    @BeforeEach
    override fun setUp() {
        super.setUp()
    }

    @AfterEach
    override fun tearDown() {
        gitlabHttp.let { it?.mockWebServer?.shutdown() }
        githubHttp.let { it?.mockWebServer?.shutdown() }

        super.tearDown()
    }

    @Test
    fun `test auth data extraction and issue creation from github user`() {
        val githubHost = GitHostFactory().createGitHost(project, githubGitUser)
        assertEquals(AuthData(
            defaultGithubAccount.id,
            githubGitUser
        ), githubHost.authData)

        this.githubHttp = BaseHttpTest<GithubIssueRequest, GithubHost>(
            githubHost as GithubHost?,
            GithubIssueRequest("title", "description", null, null, null)
        )

        githubHttp!!.testInvalidRepo(
            GithubHost(
                project,
                GitUser(
                    defaultGithubAccount.name,
                    defaultGithubAccount.server.schema + "://" + defaultGithubAccount.server.host,
                    GitProjectInfo("${defaultGithubAccount.name}/none", "git@${defaultGithubAccount.server.toString()}:${defaultGithubAccount.name}/Test.git"),
                    Platform.GITHUB
                )
            ),
            githubHttp!!.defaultRequest
        )

        githubHttp!!.testCreateIssue()

        githubHttp!!.testCreateIssueFailure(
            GithubIssueRequest(
                null,
                "",
                mutableSetOf(),
                mutableSetOf(),
                ""
            )
        )
    }

    @Test
    fun `test auth data extraction and issue creation from gitlab user`() {
        val gitlabHost = GitHostFactory().createGitHost(project, gitlabGitUser)
        assertEquals(AuthData(
            defaultGitlabAccount.id,
            gitlabGitUser
        ), gitlabHost.authData)

        this.gitlabHttp = BaseHttpTest<GitlabIssueRequest, GitlabHost>(
            gitlabHost as GitlabHost?,
            GitlabIssueRequest("title", "description", null, null, null)
        )

        gitlabHttp!!.testInvalidRepo(
            GitlabHost(
                project,
                GitUser(
                    defaultGitlabAccount.name,
                    defaultGitlabAccount.server.toString(),
                    GitProjectInfo("${defaultGitlabAccount.name}/none", "git@${defaultGitlabAccount.server.toString()}:${defaultGitlabAccount.name}/Test.git"),
                    Platform.GITLAB
                )
            ),
            gitlabHttp!!.defaultRequest
        )

        gitlabHttp!!.testCreateIssue()

        gitlabHttp!!.testCreateIssueFailure(
            GitlabIssueRequest(
                null,
                "",
                mutableSetOf(),
                mutableSetOf(),
                ""
            )
        )
    }

    override fun getName(): String {
        return "GitHostTest"
    }

}