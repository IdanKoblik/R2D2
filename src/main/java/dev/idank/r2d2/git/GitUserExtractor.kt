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

import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.Project
import dev.idank.r2d2.git.data.GitUser
import dev.idank.r2d2.git.data.UserData
import dev.idank.r2d2.managers.UserManager
import kotlinx.coroutines.runBlocking
import org.jetbrains.plugins.github.authentication.GHAccountsUtil.accounts
import org.jetbrains.plugins.github.util.GHCompatibilityUtil.getOrRequestToken
import org.jetbrains.plugins.gitlab.authentication.accounts.PersistentGitLabAccountManager
import java.time.Duration
import java.time.Instant
import java.util.*

object GitUserExtractor {
    private data class CacheEntry(
        val users: EnumMap<Platform, UserData> = EnumMap(Platform::class.java),
        val timestamp: Instant = Instant.now()
    )

    private var cache: CacheEntry = CacheEntry()
    private val LOG = logger<GitUserExtractor>()
    private val CACHE_DURATION = Duration.ofMinutes(5)

    private fun String.normalizeUrl() = replace(Regex("^https?://"), "")

    fun extractUsers(
        project: Project,
        gitUser: GitUser?,
        platform: Platform,
        force: Boolean
    ) = synchronized(this) {
        if (!force && Duration.between(cache.timestamp, Instant.now()) < CACHE_DURATION) {
            return@synchronized
        }

        try {
            when {
                gitUser != null && platform == Platform.GITHUB -> extractGithubUserData(project, gitUser)
                gitUser != null && platform == Platform.GITLAB -> extractGitlabUserData(gitUser)
            }
        } catch (e: Exception) {
            LOG.warn("Failed to extract user data for $platform", e)
        }
    }

    private fun extractGithubUserData(
        project: Project,
        user: GitUser
    ) {
        val normalizedInstance = user.instance.normalizeUrl()

        accounts.firstOrNull { account ->
            account.name == user.username && account.server.toString().normalizeUrl() == normalizedInstance
        }?.let { account ->
            val token = getOrRequestToken(account, project)
            if (token != null) {
                val data = UserData(
                    account.name,
                    token,
                    user.instance,
                    user.namespace,
                    user.url
                )

                cache.users[Platform.GITHUB] = data
                UserManager.getInstance().addUserData(user, data)
            }
        }
    }

    private fun extractGitlabUserData(user: GitUser) {
        val accountManager = PersistentGitLabAccountManager()
        val normalizedUserInstance = user.instance.normalizeUrl()
        val gitlabAccounts = accountManager.accountsState.value

        for (account in gitlabAccounts) {
            if (account.name != user.username ||
                account.server.toString().normalizeUrl() != normalizedUserInstance) {
                continue
            }

            val credentials = runBlocking {
                accountManager.findCredentials(account)
            }

            val data = UserData(
                user.username,
                credentials.toString(),
                user.instance,
                user.namespace,
                user.url
            )

            cache.users[Platform.GITLAB] = data
            UserManager.getInstance().addUserData(user, data)
            break
        }
    }

    fun getUserData(platform: Platform): UserData? = synchronized(this) {
        return cache.users[platform]
    }

    fun invalidateCache() = synchronized(this) {
        cache = CacheEntry()
    }

}