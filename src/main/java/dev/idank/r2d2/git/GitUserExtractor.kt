package dev.idank.r2d2.git

import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.Project
import dev.idank.r2d2.git.data.GitUser
import dev.idank.r2d2.git.data.UserData
import dev.idank.r2d2.managers.UserManager
import kotlinx.coroutines.runBlocking
import org.jetbrains.annotations.TestOnly
import org.jetbrains.plugins.github.authentication.GHAccountsUtil.accounts
import org.jetbrains.plugins.github.util.GHCompatibilityUtil.getOrRequestToken
import org.jetbrains.plugins.gitlab.authentication.accounts.PersistentGitLabAccountManager
import java.time.Duration
import java.time.Instant
import java.util.*

class GitUserExtractor private constructor() {
    private data class CacheEntry(
        val users: EnumMap<Platform, UserData>,
        val timestamp: Instant = Instant.now()
    )

    private var cache: CacheEntry? = null

    companion object {
        private val LOG = logger<GitUserExtractor>()
        private val CACHE_DURATION = Duration.ofMinutes(5)

        @Volatile
        private var instance: GitUserExtractor? = null

        @JvmStatic
        fun getInstance(): GitUserExtractor =
            instance ?: synchronized(this) {
                instance ?: GitUserExtractor().also { instance = it }
            }

        @TestOnly
        @JvmStatic
        fun resetInstance() {
            instance = null
        }

        private fun String.normalizeUrl() =
            replace(Regex("^https?://"), "")
    }

    fun extractUsers(
        project: Project,
        gitUser: GitUser?,
        platform: Platform,
        force: Boolean
    ): Map<Platform, UserData> = synchronized(this) {
        val currentCache = cache
        if (currentCache != null && Duration.between(currentCache.timestamp, Instant.now()) < CACHE_DURATION && !force)
            return@synchronized currentCache.users.toMap()

        val newUsers = EnumMap<Platform, UserData>(Platform::class.java)
        try {
            when {
                gitUser != null && platform == Platform.GITHUB ->
                    extractGithubUserData(project, gitUser, newUsers)
                gitUser != null && platform == Platform.GITLAB ->
                    extractGitlabUserData(gitUser, newUsers)
            }
        } catch (e: Exception) {
            LOG.warn("Failed to extract user data for $platform", e)
        }

        cache = CacheEntry(newUsers)
        newUsers.toMap()
    }

    private fun extractGithubUserData(
        project: Project,
        user: GitUser,
        users: EnumMap<Platform, UserData>
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

                users[Platform.GITHUB] = data
                UserManager.getInstance().addUserData(user, data)
            }
        }
    }

    private fun extractGitlabUserData(
        user: GitUser,
        users: EnumMap<Platform, UserData>
    ) {
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

            users[Platform.GITLAB] = data
            UserManager.getInstance().addUserData(user, data)
            break
        }
    }

    fun invalidateCache() = synchronized(this) {
        cache = null
    }

    @TestOnly
    fun getCachedUsers(): Map<Platform, UserData>? = synchronized(this) {
        cache?.users?.toMap()
    }

    @TestOnly
    fun setCachedUsers(users: EnumMap<Platform, UserData>?) = synchronized(this) {
        cache = users?.let { CacheEntry(it) }
    }
}