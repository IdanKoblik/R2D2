package dev.idank.r2d2

import com.intellij.openapi.GitRepositoryInitializer
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.testFramework.LightPlatformTestCase
import com.intellij.testFramework.PlatformTestUtil
import dev.idank.r2d2.git.Platform
import dev.idank.r2d2.git.data.GitProjectInfo
import dev.idank.r2d2.git.data.GitUser
import git4idea.GitVcs
import git4idea.commands.Git
import git4idea.commands.GitCommand
import git4idea.commands.GitLineHandler
import git4idea.repo.GitRepository
import git4idea.repo.GitRepositoryManager
import kotlinx.coroutines.runBlocking
import org.jetbrains.plugins.github.api.GithubServerPath
import org.jetbrains.plugins.github.authentication.accounts.GithubAccount
import org.jetbrains.plugins.gitlab.api.GitLabServerPath
import org.jetbrains.plugins.gitlab.authentication.accounts.GitLabAccount
import java.io.File

open class GitBaseTest : LightPlatformTestCase() {

    protected var defaultGithubAccount: GithubAccount = GithubAccount(
        System.getProperty("github.user"),
        GithubServerPath("github.com"),
        System.getProperty("github.token")
    )

    protected var defaultGitlabAccount: GitLabAccount = GitLabAccount(
        System.getProperty("gitlab.token"),
        System.getProperty("gitlab.user"),
        GitLabServerPath("https://gitlab.com")
    )

    protected lateinit var git: Git
    protected lateinit var repository: GitRepository
    protected lateinit var projectRoot: VirtualFile

    protected val gitlabGitUser = GitUser(
        defaultGitlabAccount.name,
        defaultGitlabAccount.server.toString(),
        GitProjectInfo("${defaultGitlabAccount.name}/testing", "git@${defaultGitlabAccount.server.toString()}:${defaultGitlabAccount.name}/Test.git"),
        Platform.GITLAB
    )

    protected val githubGitUser = GitUser(
        defaultGithubAccount.name,
        defaultGithubAccount.server.schema + "://" + defaultGithubAccount.server.toString(),
        GitProjectInfo("${defaultGithubAccount.name}/testing", "git@${defaultGithubAccount.server.host}:${defaultGithubAccount.name}/Test.git"),
        Platform.GITHUB
    )

    override fun setUp() {
        super.setUp()

        runBlocking {
            githubAccountManager.updateAccount(defaultGithubAccount, defaultGithubAccount.id)
            gitlabAccountManager.updateAccount(defaultGitlabAccount, defaultGitlabAccount.id)
        }

        projectRoot = PlatformTestUtil.getOrCreateProjectBaseDir(project)
        git = Git.getInstance()

        GitVcs.getInstance(project).doActivate()
        GitRepositoryInitializer.getInstance()?.initRepository(project, projectRoot)

        repository = GitRepositoryManager.getInstance(project).getRepositoryForRoot(projectRoot)!!

        val handler = GitLineHandler(project, projectRoot, GitCommand.CONFIG)
        handler.addParameters("user.email", "test@gitlab.com")
        git.runCommand(handler)

        val output = git.addRemote(
            repository,
            "gfdgfdgfd",
            "https://github.com/Test/test.git"
        )

        println(output.outputAsJoinedString)
        println(repository.remotes)
        /*        val githubRemoteHandler = GitLineHandler(project, projectRoot, GitCommand.REMOTE)
                githubRemoteHandler.addParameters("add", "github", "git@github.com:${defaultGithubAccount.name}/Test.git")
                git.runCommand(githubRemoteHandler)*/
    }

    override fun tearDown() {
        try {
            // Remove the .git directory recursively from the project root
            val gitDirectory = File(projectRoot.path, ".git")
            if (gitDirectory.exists() && gitDirectory.isDirectory) {
                deleteDirectoryRecursively(gitDirectory)
            }

            // Optionally, you can also delete the projectRoot directory itself if needed
            // deleteDirectoryRecursively(projectRoot)

        } catch (e: Exception) {
            println("Error during tearDown: ${e.message}")
        }
    }

    private fun deleteDirectoryRecursively(directory: File) {
        if (directory.isDirectory) {
            directory.listFiles()?.forEach { file ->
                deleteDirectoryRecursively(file)
            }
        }
        directory.delete()
    }


}