package dev.idank.r2d2.git

import com.intellij.openapi.project.Project
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.jetbrains.annotations.TestOnly
import org.jetbrains.plugins.github.authentication.GHAccountsUtil.accounts
import org.jetbrains.plugins.github.util.GHCompatibilityUtil.getOrRequestToken
import org.jetbrains.plugins.gitlab.authentication.accounts.PersistentGitLabAccountManager
import java.util.*

private const val CACHE_EXPIRY_TIME_MS = (5 * 60 * 1000).toLong() // 5 minutes

class GitUserExtractor private constructor() {
    private var cachedUsers: EnumMap<Platform, UserData>? = null

    @get:Synchronized
    @set:Synchronized
    @set:TestOnly
    var lastCacheTime: Long = -1

    @Synchronized
    fun extractUsers(project: Project, gitUser: GitUser?, platform: Platform): Map<Platform, UserData> {
        if (cachedUsers == null || System.currentTimeMillis() - lastCacheTime > CACHE_EXPIRY_TIME_MS) {
            this.cachedUsers = EnumMap(Platform::class.java)

            if (gitUser != null && platform == Platform.GITHUB)
                extractGithubUserData(project, gitUser)

            if (gitUser != null && platform == Platform.GITLAB)
                extractGitLabUserData(project, gitUser)

            lastCacheTime = System.currentTimeMillis()
        }

        return Collections.unmodifiableMap(cachedUsers!!)
    }

    private fun extractGithubUserData(project: Project, user: GitUser) {
        try {
            for (account in accounts) {
                if (account.name == user.username && account.server.toString() == user.instance.replace("https://", "").replace("http://", "")) {
                    synchronized(this) {
                        cachedUsers!!.put(
                            Platform.GITHUB, UserData(
                                account.name,
                                getOrRequestToken(account, project)!!,
                                user.instance,
                                user.namespace,
                                user.url
                            )
                        )
                    }

                    break
                }
            }
        } catch (e: Exception) {
            println("Failed to fetch GitHub token: " + e.message)
        }
    }

    private fun extractGitLabUserData(project: Project, gitlabUser: GitUser) {
        try {
            val persistentGitLabAccountManager = PersistentGitLabAccountManager()
            val users = persistentGitLabAccountManager.accountsState.value.associateWith { user ->
                runBlocking(Dispatchers.IO) {
                    persistentGitLabAccountManager.findCredentials(user).toString()
                }
            }

            for (user in users.keys) {
                val normalizedServerUrl = user.server.toString()
                    .replace("https://", "")
                    .replace("http://", "")

                if (user.name == gitlabUser.username && normalizedServerUrl == gitlabUser.instance.replace("https://", "").replace("http://", "")) {
                    synchronized(this) {
                        cachedUsers!!.put(
                            Platform.GITLAB, UserData(
                                gitlabUser.username,
                                users[user].toString(),
                                gitlabUser.instance,
                                gitlabUser.namespace,
                                gitlabUser.url
                            )
                        )
                    }
                    break
                }
            }
        } catch (e: Exception) {
            println("Error extracting GitLab user data: ${e.message}")
            e.printStackTrace()
        }
    }

    @Synchronized
    fun invalidateCache() {
        this.cachedUsers = null
        this.lastCacheTime = -1
    }

    @TestOnly
    @Synchronized
    fun getCachedUsers(): Map<Platform, UserData>? {
        return if (cachedUsers != null) Collections.unmodifiableMap(cachedUsers) else null
    }

    @TestOnly
    @Synchronized
    fun setCachedUsers(cachedUsers: EnumMap<Platform, UserData>?) {
        this.cachedUsers = cachedUsers
    }

    companion object {
        private var instance: GitUserExtractor? = null

        @Synchronized
        fun getInstance(): GitUserExtractor {
            if (instance == null) {
                instance = GitUserExtractor()
            }

            return instance!!
        }

        @TestOnly
        fun resetInstance() {
            instance = null
        }
    }
}