/*
MIT License

Copyright (c) 2025 Idan Koblik

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/
package dev.idank.r2d2.git

import dev.idank.r2d2.GitTest
import dev.idank.r2d2.git.data.AuthData
import dev.idank.r2d2.git.data.GitProjectInfo
import dev.idank.r2d2.git.data.GitUser
import dev.idank.r2d2.git.request.GithubIssueRequest
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GithubIssueTest : GitTest() {

    private lateinit var githubHost: GithubHost
    private lateinit var githubGitUser: GitUser

    @BeforeEach
    override fun setUp() {
        platform = Platform.GITHUB
        super.setUp()

        githubGitUser = GitUser(
            defaultGithubAccount.name,
            "${defaultGithubAccount.server.schema}://${defaultGithubAccount.server.host}",
            GitProjectInfo(
                "${defaultGithubAccount.name}/${Platform.GITHUB.name}",
                "git@${defaultGithubAccount.server.host}:${defaultGithubAccount.name}/${Platform.GITHUB.name}.git"
            ),
            Platform.GITHUB
        )

        githubHost = GitHostFactory().createGitHost(project, githubGitUser) as GithubHost

        assertEquals(
            AuthData(defaultGithubAccount.id, githubGitUser),
            githubHost.authData
        )
    }

    @AfterEach
    override fun tearDown() {
        githubHost.client.dispatcher.executorService.shutdown()
        githubHost.client.connectionPool.evictAll()

        super.tearDown()
    }

    @Test
    fun `test create issue with all fields`() {
        //enqueueResponse(200, "{}")

        val issueRequest = GithubIssueRequest(
            "Test Issue",
            "Test Description",
            setOf(),
            setOf(),
            null
        )

        //val response = githubHost.createIssue(issueRequest)
        //Assertions.assertNotNull(response)
    }

    @Test
    fun `test create issue with error response`() {
        enqueueResponse(400, "{\"message\": \"Bad Request\"}")

        val issueRequest = GithubIssueRequest(
            null,
            null,
            setOf(),
            setOf(),
            null
        )

        val response = githubHost.createIssue(issueRequest)
        Assertions.assertNotNull(response)
    }


    private fun enqueueResponse(statusCode: Int, body: String) {
        mockWebServer!!.enqueue(MockResponse().setResponseCode(statusCode).setBody(body))
    }
}