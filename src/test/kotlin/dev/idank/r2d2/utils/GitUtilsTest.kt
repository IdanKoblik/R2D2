package dev.idank.r2d2.utils

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.File
import java.io.IOException

class GitUtilsTest {

    @Test
    fun `test extractGitInfo with SSH URL`() {
        val testRepoPath = createTempDir().absolutePath

        val configFile = File("$testRepoPath/.git/config")
        configFile.parentFile.mkdirs()
        configFile.writeText("""
            [remote "origin"]
                url = git@github.com:username/repository.git
            """.trimIndent())

        val result = GitUtils.extractGitInfo(testRepoPath)
        assertEquals("github.com", result.first)
        assertEquals("username/repository", result.second)
    }

    @Test
    fun `test extractGitInfo with HTTPS URL`() {
        val testRepoPath = createTempDir().absolutePath

        val configFile = File("$testRepoPath/.git/config")
        configFile.parentFile.mkdirs()
        configFile.writeText("""
            [remote "origin"]
                url = https://github.com/username/repository.git
            """.trimIndent())

        val result = GitUtils.extractGitInfo(testRepoPath)
        assertEquals("github.com", result.first)
        assertEquals("username/repository", result.second)
    }

    @Test
    fun `test extractGitInfo should throw error if config not found`() {
        val testRepoPath = createTempDir().absolutePath

        val exception = org.junit.jupiter.api.assertThrows<IOException> {
            GitUtils.extractGitInfo(testRepoPath)
        }
        assertEquals("Cannot find git config file", exception.message)
    }
}
