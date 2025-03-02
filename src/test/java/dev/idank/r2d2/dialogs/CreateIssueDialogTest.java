package dev.idank.r2d2.dialogs;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.psi.PsiFile;
import dev.idank.r2d2.GitTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CreateIssueDialogTest extends GitTest {

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
    public void testOkAction() {
        PsiFile file = myFixture.configureByText("temp.txt", "test");

        ApplicationManager.getApplication().invokeLater(() -> {
            CreateIssueDialog dialog = new CreateIssueDialog(
                    project, pluginLoader, "issue", "body", 0, getMyFixture().getDocument(file)
            );

            dialog.doOKAction();
            assertTrue(getMyFixture().getDocument(file).getText().startsWith("test https://github.com/IdanKoblik/github/issues/"));
        });
    }
}
