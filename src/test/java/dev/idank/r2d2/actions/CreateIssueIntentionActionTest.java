package dev.idank.r2d2.actions;

import dev.idank.r2d2.GitTest;
import dev.idank.r2d2.PluginLoader;
import dev.idank.r2d2.services.PluginLoaderService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CreateIssueIntentionActionTest extends GitTest {

    private CreateIssueIntentionAction action;
    private PluginLoader pluginLoader;

    @BeforeEach
    @Override
    protected void setUp() {
        super.setUp();

        this.pluginLoader = project.getService(PluginLoaderService.class).getPluginLoader();
        this.action  = new CreateIssueIntentionAction(
                "test", "test", 2, pluginLoader
        );
    }

    @AfterEach
    @Override
    protected void tearDown() {
        super.tearDown();
    }

    @Test
    void testisAvailable() {
        assertTrue(action.isAvailable(myFixture.getProject(), myFixture.getEditor(), myFixture.getFile()));

        CreateIssueIntentionAction invalidAction = new CreateIssueIntentionAction(
                "test", "test", -1, pluginLoader
        );

        assertFalse(invalidAction.isAvailable(myFixture.getProject(), myFixture.getEditor(), myFixture.getFile()));
    }

    @Test
    public void testInvoke() {
        myFixture.configureByText("test.txt", "test");

        assertDoesNotThrow(() -> {
            action.invoke(myFixture.getProject(), myFixture.getEditor(), myFixture.getFile());
        });
    }

    @Test
    void testName() {
        assertEquals(CreateIssueIntentionAction.NAME, action.getFamilyName());
        assertEquals(CreateIssueIntentionAction.NAME, action.getText());
    }
}
