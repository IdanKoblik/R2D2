package dev.idank.r2d2.actions;

import com.intellij.testFramework.EdtTestUtil;
import dev.idank.r2d2.GitTest;
import dev.idank.r2d2.PluginLoader;
import dev.idank.r2d2.services.PluginLoaderService;
import dev.idank.r2d2.utils.PluginUtils;
import git4idea.repo.GitRepositoryManager;
import org.assertj.swing.annotation.GUITest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InvalidateCachesActionTest extends GitTest {

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
    @GUITest
    public void testInvalidateCaches() {
        myFixture.configureByText("test.txt", "test");

        PluginLoader pluginLoader = project.getService(PluginLoaderService.class).getPluginLoader();
        pluginLoader.onEnable(project, GitRepositoryManager.getInstance(project).getRepositories().stream().findFirst().orElseThrow());
        assertEquals(1, pluginLoader.getIssueData().size());

        PluginUtils.INSTANCE.updateGithubAccount(githubAccountManager, getDefaultGithubAccount(), PluginUtils.Action.REMOVE);

        EdtTestUtil.runInEdtAndWait(() -> {
            myFixture.testAction(new InvalidateCachesAction());
        });
        assertEquals(0, pluginLoader.getIssueData().size());
    }
}
