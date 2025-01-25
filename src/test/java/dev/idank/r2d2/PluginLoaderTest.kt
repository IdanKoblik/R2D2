package dev.idank.r2d2

import com.intellij.testFramework.fixtures.*
import dev.idank.r2d2.dialogs.CreateIssueDialog
import dev.idank.r2d2.git.Platform
import dev.idank.r2d2.managers.GitManager
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths

class PluginLoaderTest : BaseTest() {

    private lateinit var gitConfig: File

    @BeforeEach
    override fun setUp() {
        super.setUp()
        gitConfig = File(myFixture.project.basePath, ".git/config")
        gitConfig.parentFile.mkdirs()
    }

    @AfterEach
    override fun tearDown() {
        super.tearDown()
        GitManager.getInstance().clear()
    }

    @Test
    fun `test getting github accounts info`() {
        writeConfigContent("[remote \"origin\"]\n\turl = https://github.com/tester/repo.git\n")

        GitManager.getInstance().addNamespace("https://github.com/tester/repo.git")
        val accounts = PluginLoader.getInstance().getGitAccounts(
            this.defaultGithubAccount,
            this.defaultGitlabAccount,
        )

        assertEquals(1, accounts.size)
        assertEquals("tester-GH / https://github.com / GITHUB [tester/repo]", accounts.first().toString())
    }

    @Test
    fun `test getting gitlab accounts info`() {
        writeConfigContent("[remote \"origin\"]\n\turl = https://gitlab.com/tester/repo.git\n")

        GitManager.getInstance().addNamespace("https://gitlab.com/tester/repo.git")
        val accounts = PluginLoader.getInstance().getGitAccounts(
            this.defaultGithubAccount,
            this.defaultGitlabAccount,
        )

        assertEquals(1, accounts.size)
        assertEquals("tester-GL / https://gitlab.com / GITLAB [tester/repo]", accounts.first().toString())
    }

    @Test
    fun `test create invalid git user`() {
        writeConfigContent("[remote \"origin\"]\n\turl = https://gitlab.com/tester/repo.git\n")

        GitManager.getInstance().addNamespace("https://gitlab.com/tester/repo.git")
        val user = PluginLoader.getInstance().createGitUser(
            CreateIssueDialog.NO_USER,
        )

        assertNull(user)
    }

    @Test
    fun `test create git user`() {
        writeConfigContent("[remote \"origin\"]\n\turl = https://gitlab.com/tester/repo.git\n")

        GitManager.getInstance().addNamespace("https://gitlab.com/tester/repo.git")
        val user = PluginLoader.getInstance().createGitUser(
            "tester-GL / https://gitlab.com / GITLAB [tester/repo]",
        )

        assertEquals(user.username, "tester-GL")
        assertEquals(user.platform, Platform.GITLAB)
        assertEquals(user.url, "https://gitlab.com/tester/repo.git")
        assertEquals(user.instance, "https://gitlab.com")
        assertEquals(user.namespace, "tester/repo")
    }

    @Throws(IOException::class)
    private fun writeConfigContent(content: String) {
        Files.write(Paths.get(gitConfig.toURI()), content.toByteArray())
    }
}
