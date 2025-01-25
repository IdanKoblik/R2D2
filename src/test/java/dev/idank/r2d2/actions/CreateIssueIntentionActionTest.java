package dev.idank.r2d2.actions;

import dev.idank.r2d2.BaseTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CreateIssueIntentionActionTest extends BaseTest {

    private CreateIssueIntentionAction action = new CreateIssueIntentionAction(
            "test", "test", 2
    );

    @BeforeEach
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @AfterEach
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    void testInvoke() {
        assertDoesNotThrow(() -> {
            action.invoke(myFixture.getProject(), myFixture.getEditor(), myFixture.getFile());
        });
    }

    @Test
    void testisAvailable() {
        assertTrue(action.isAvailable(myFixture.getProject(), myFixture.getEditor(), myFixture.getFile()));

        CreateIssueIntentionAction invalidAction = new CreateIssueIntentionAction(
                "test", "test", -1
        );

        assertFalse(invalidAction.isAvailable(myFixture.getProject(), myFixture.getEditor(), myFixture.getFile()));
    }

    @Test
    void testName() {
        assertEquals(CreateIssueIntentionAction.NAME, action.getFamilyName());
        assertEquals(CreateIssueIntentionAction.NAME, action.getText());
    }
}
