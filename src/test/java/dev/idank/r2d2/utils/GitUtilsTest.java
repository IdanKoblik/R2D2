/*
package dev.idank.r2d2.utils;

import dev.idank.r2d2.dialogs.CreateIssueDialog;
import dev.idank.r2d2.git.data.GitInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class GitUtilsTest {

    private CreateIssueDialog dialog;

    private Path tempDir;
    private File gitConfig;

    @BeforeEach
    public void setUp() throws IOException {
        tempDir = Files.createTempDirectory("testRepo");

        gitConfig = new File(tempDir.toFile(), ".git/config");
        gitConfig.getParentFile().mkdirs();
    }

    private void writeConfigContent(String content) throws IOException {
        Files.write(Paths.get(gitConfig.toURI()), content.getBytes());
    }

    @Test
    public void testExtractGitInfo_ValidHttpUrl() throws IOException {
        writeConfigContent("[remote \"origin\"]\n\turl = https://github.com/user/repo.git\n");

        Map<String, GitInfo> result = GitUtils.extractGitInfo(tempDir.toString(), dialog);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.containsKey("github.com"));
        assertEquals("user/repo", result.get("github.com").namespace());
        assertEquals("https://github.com/user/repo.git", result.get("github.com").url());
    }

    @Test
    public void testExtractGitInfo_ValidSshUrl() throws IOException {
        writeConfigContent("[remote \"origin\"]\n\turl = git@github.com:user/repo.git\n");

        Map<String, GitInfo> result = GitUtils.extractGitInfo(tempDir.toString(), dialog);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.containsKey("github.com"));
        assertEquals("user/repo", result.get("github.com").namespace());
        assertEquals("git@github.com:user/repo.git", result.get("github.com").url());
    }

    @Test
    public void testExtractGitInfo_InvalidUrlFormat() throws IOException {
        writeConfigContent("[remote \"origin\"]\n\turl = ftp://github.com/user/repo.git\n");

        Map<String, GitInfo> result = GitUtils.extractGitInfo(tempDir.toString(), dialog);

        assertTrue(result.isEmpty());
    }

    @Test
    public void testExtractGitInfo_MultipleRemoteUrls() throws IOException {
        String configContent = "[remote \"origin\"]\n\turl = https://github.co.il/user/repo.git\n" +
                "[remote \"upstream\"]\n\turl = git@github.com:anotheruser/anotherrepo.git\n";
        writeConfigContent(configContent);

        Map<String, GitInfo> result = GitUtils.extractGitInfo(tempDir.toString(), dialog);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.containsKey("github.com"));
        assertEquals("user/repo", result.get("github.co.il").namespace());
        assertEquals("https://github.co.il/user/repo.git", result.get("github.co.il").url());
        assertEquals("anotheruser/anotherrepo", result.get("github.com").namespace());
        assertEquals("git@github.com:anotheruser/anotherrepo.git", result.get("github.com").url());
    }

    @Test
    public void testExtractGitInfo_EmptyConfigFile() throws IOException {
        writeConfigContent("");

        Map<String, GitInfo> result = GitUtils.extractGitInfo(tempDir.toString(), dialog);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testExtractGitInfo_NoRemoteUrl() throws IOException {
        writeConfigContent("[remote \"origin\"]\n");

        Map<String, GitInfo> result = GitUtils.extractGitInfo(tempDir.toString(), dialog);
        assertTrue(result.isEmpty());
    }

}
*/
