package dev.idank.r2d2.api

interface GitApi {
    fun createIssue()
    fun fetchProjectId(): Int
}
