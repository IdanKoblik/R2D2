package dev.idank.r2d2.api

sealed class Issue(val token: String, val title: String) {
    data class GitlabIssue(
        val gitlabToken: String,
        val gitlabTitle: String,
        val instance: String,
        val namespace: String
    ) : Issue(gitlabToken, gitlabTitle)
}