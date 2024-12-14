import dev.idank.r2d2.api.AbstractGitApi
import dev.idank.r2d2.api.Issue
import dev.idank.r2d2.api.RequestMethod
import javax.swing.JOptionPane

class GitlabApiImpl(issue: Issue.GitlabIssue) : AbstractGitApi(issue) {

    val gitlabIssue = issue as Issue.GitlabIssue

    override fun createIssue() {
        val projectId = fetchProjectId()
        if (projectId == -1) return

        val url = "https://${gitlabIssue.instance}/api/v4/projects/$projectId/issues"
        val body = """{"title": "${gitlabIssue.title}"}"""
        val response = sendRequest(RequestMethod.POST, url, body)

        if (response != null)
            JOptionPane.showMessageDialog(null, "GitLab Issue created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE)
        else
            JOptionPane.showMessageDialog(null, "Failed to create GitLab issue.", "Error", JOptionPane.ERROR_MESSAGE)
    }

    override fun fetchProjectId(): Int {
        val url = "https://${gitlabIssue.instance}/api/v4/projects"
        val response = sendRequest(RequestMethod.GET, url)

        val jsonArray = objectMapper.readTree(response)
        for (project in jsonArray) {
            if (project.path("path_with_namespace").asText() == gitlabIssue.namespace)
                return project.path("id").asInt()
        }

        JOptionPane.showMessageDialog(null, "No GitLab project found for namespace: ${gitlabIssue.namespace}", "Project Not Found", JOptionPane.INFORMATION_MESSAGE)
        return -1
    }

    override fun getProjectUrl(): String {
        return "https://${gitlabIssue.instance}/api/v4/projects/${gitlabIssue.namespace}"
    }
}
