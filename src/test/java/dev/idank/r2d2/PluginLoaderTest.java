package dev.idank.r2d2;

import git4idea.repo.GitRepositoryManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PluginLoaderTest extends GitTest {

    @BeforeEach
    @Override
    protected void setUp() {
        super.setUp();
    }

    @AfterEach
    @Override
    protected void tearDown() {
        super.tearDown();
    }

    @Test
    public void testOnEnable() {
        PluginLoader loader = new PluginLoader();
        loader.onEnable(project, GitRepositoryManager.getInstance(project).getRepositories().stream().findFirst().orElseThrow());

        assertNotNull(loader.getGitManager());
        assertNotNull(loader.getGitAccounts());
        assertNotNull(loader.getIssueData());
        assertNotNull(loader.getGitRepository());
        assertFalse(loader.getIssueData().isEmpty());
    }
}
