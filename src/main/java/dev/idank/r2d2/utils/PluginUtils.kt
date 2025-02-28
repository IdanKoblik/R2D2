package dev.idank.r2d2.utils

import kotlinx.coroutines.runBlocking
import org.jetbrains.plugins.github.authentication.accounts.GHAccountManager
import org.jetbrains.plugins.github.authentication.accounts.GithubAccount
import org.jetbrains.plugins.gitlab.authentication.accounts.GitLabAccount
import org.jetbrains.plugins.gitlab.authentication.accounts.PersistentGitLabAccountManager

object PluginUtils {

    fun updateGitlabAccount(gitLabAccountManager: PersistentGitLabAccountManager, account: GitLabAccount, action: Action) {
        runBlocking {
            if (action == Action.INSERT)
                gitLabAccountManager.updateAccount(account, account.id)
            else if (action == Action.REMOVE)
                gitLabAccountManager.removeAccount(account)
        }
    }

    fun updateGithubAccount(githubAccountManager: GHAccountManager, account: GithubAccount, action: Action) {
        runBlocking {
            if (action == Action.INSERT)
                githubAccountManager.updateAccount(account, account.id)
            else if (action == Action.REMOVE)
                githubAccountManager.removeAccount(account)
        }
    }

    fun findGitlabUserCredentials(account: GitLabAccount) : String {
        val accountManager = PersistentGitLabAccountManager()
        return runBlocking {
            accountManager.findCredentials(account).toString()
        }
    }

    enum class Action {
        INSERT,
        REMOVE
    }
}