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