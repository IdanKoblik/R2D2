/*
package dev.idank.r2d2;

import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;

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
        Optional<GitRepository> repoOpt = GitRepositoryManager.getInstance(project).getRepositories().stream().findFirst();
        assertFalse(repoOpt.isEmpty());

        PluginLoader loader = new PluginLoader();
        loader.onEnable(
                project,
                repoOpt.get()
        );

        System.out.println(loader.getIssueData());
    }
}
*/
