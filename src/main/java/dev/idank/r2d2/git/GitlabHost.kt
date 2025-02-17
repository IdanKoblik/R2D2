package dev.idank.r2d2.git

import com.intellij.openapi.project.Project
import com.intellij.util.io.URLUtil
import dev.idank.r2d2.git.data.AuthData
import dev.idank.r2d2.git.data.GitUser
import dev.idank.r2d2.git.data.issue.IssueData
import dev.idank.r2d2.git.request.GitlabIssueRequest
import dev.idank.r2d2.git.request.IssueRequest
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.jetbrains.plugins.gitlab.authentication.accounts.PersistentGitLabAccountManager
import java.io.IOException

val accountManager = PersistentGitLabAccountManager()

class GitlabHost(project: Project?, user: GitUser?) : GitHost<GitlabIssueRequest>(project, user) {

    private var namespace: String? = null

    init {
        this.authData = authData(project, user)
        this.namespace = URLUtil.encodeURIComponent(this.authData.user.projectInfo.namespace);
    }

    override fun authData(project: Project?, user: GitUser?): AuthData? {
        val url = normalizeURL(user?.instance)
        val accounts = accountManager.accountsState.value

        for (account in accounts) {
            if (account.name != user?.username || normalizeURL(account.server.toString()) != url)
                continue

            val credentials = runBlocking {
                accountManager.findCredentials(account)
            }

            return AuthData(
                credentials.toString(),
                user
            )
        }

        return null
    }

    @Throws(IOException::class)
    override fun createIssue(request: IssueRequest): Response? {
        val url = "${authData.user.instance}/api/v4/projects/${this.namespace}/issues"
        val body = this.objectMapper.writeValueAsBytes(request).toRequestBody(
            "application/json".toMediaType()
        )

        val request = Request.Builder()
            .url(url)
            .header("Authorization", "Bearer " + authData.token)
            .post(body)
            .build()

        return client.newCall(request).execute()
    }

    override fun fetchIssueData(): IssueData {
        val url = "${authData.user.instance}/api/v4/projects/${this.namespace}"
        return IssueData(
            fetchIssues("$url/labels"),
            fetchUsers("$url/users?exclude_bots=true", "username"),
            fetchMilestones("$url/milestones", "id", "active")
        )
    }


}