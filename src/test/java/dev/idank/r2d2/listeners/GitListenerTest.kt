package dev.idank.r2d2.listeners

import dev.idank.r2d2.BaseTest
import dev.idank.r2d2.git.GitUserExtractor
import dev.idank.r2d2.git.Platform
import dev.idank.r2d2.git.data.GitInfo
import dev.idank.r2d2.git.data.GitUser
import dev.idank.r2d2.git.data.UserData
import dev.idank.r2d2.managers.GitManager
import dev.idank.r2d2.utils.GitUtils
import git4idea.repo.GitRepository
import kotlinx.coroutines.runBlocking
import org.jetbrains.plugins.github.authentication.accounts.GHAccountManager
import org.jetbrains.plugins.gitlab.authentication.accounts.PersistentGitLabAccountManager
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File

class GitListenerTest : BaseTest() {

    private lateinit var file: File
    private lateinit var repo: GitRepository

    @BeforeEach
    override fun setUp() {
        super.setUp()
        val dummy = GitUtils.createDummyRepo(myFixture.project)
        file = dummy.file
        repo = dummy.repo
    }

    @AfterEach
    override fun tearDown() {
        super.tearDown()

        if (file.exists())
            deleteRecursively(file)
    }

    private fun deleteRecursively(file: File) {
        if (file.isDirectory)
            file.listFiles()?.forEach { deleteRecursively(it) }

        file.delete()
    }

    @Test
    fun testExec() {
        runBlocking {
            GHAccountManager().updateAccounts(
                mapOf(defaultGithubAccount to defaultGithubAccount.id)
            )

            PersistentGitLabAccountManager().updateAccounts(
                mapOf(defaultGitlabAccount to defaultGitlabAccount.id)
            )
        }

        val listener = GitListener(myFixture.project)
        listener.repositoryChanged(
            repo
        )

        assertEquals(
            mutableSetOf(
                GitInfo(
                    "tester/repo",
                    "https://gitlab.com/tester/repo.git"
                )
            ), GitManager.getInstance().getNamespace("gitlab.com").orElse(setOf())
        )

        // Valid
        GitUserExtractor.extractUsers(myFixture.project, GitUser(
            defaultGitlabAccount.name,
            "gitlab.com",
            "tester-GL/repo",
            "git@gitlab.com:IdanKoblik/R2D2.git",
            Platform.GITLAB
        ), Platform.GITLAB, true)

        // Valid
        GitUserExtractor.extractUsers(myFixture.project, GitUser(
            defaultGithubAccount.name,
            "github.com",
            "tester-GH/repo",
            "git@github.com:IdanKoblik/R2D2.git",
            Platform.GITHUB
        ), Platform.GITHUB, true)

        // Invalid
        GitUserExtractor.extractUsers(myFixture.project, GitUser(
            "invalid",
            "invalid",
            "invalid",
            "invalid",
            Platform.GITLAB
        ), Platform.GITLAB, true)

        assertEquals(
            UserData(
            "tester-GH",
            "adf5220b-407f-48e6-8652-b10b1d105313",
            "github.com",
            "tester-GH/repo",
            "git@github.com:IdanKoblik/R2D2.git"
        ), GitUserExtractor.getUserData(Platform.GITHUB))

        assertEquals(
            UserData(
            "tester-GL",
            "adf5220b-407f-48e6-8652-b10b1d105313",
            "gitlab.com",
            "tester-GL/repo",
            "git@gitlab.com:IdanKoblik/R2D2.git"
        ), GitUserExtractor.getUserData(Platform.GITLAB))
    }
}