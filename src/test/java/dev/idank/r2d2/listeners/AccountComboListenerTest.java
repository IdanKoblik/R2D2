package dev.idank.r2d2.listeners;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.psi.PsiFile;
import dev.idank.r2d2.GitTest;
import dev.idank.r2d2.dialogs.CreateIssueDialog;
import dev.idank.r2d2.git.data.User;
import dev.idank.r2d2.git.data.issue.IssueData;
import dev.idank.r2d2.git.data.issue.Milestone;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.event.ActionEvent;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AccountComboListenerTest extends GitTest {

    public static final String USER = "IdanKoblik / https://github.com / GITHUB [IdanKoblik/github]";

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
    public void testAction() {
        PsiFile file = myFixture.configureByText("combo.txt", "test");

        ApplicationManager.getApplication().invokeLater(() -> {
            CreateIssueDialog dialog = new CreateIssueDialog(
                    project, pluginLoader, "issue", "body", 1, getMyFixture().getDocument(file)
            );

            dialog.setData(new IssueData(
                    Set.of(),
                    Set.of(),
                    Set.of()
            ));

            AccountComboListener listener = new AccountComboListener(dialog, pluginLoader);
            listener.actionPerformed(new ActionEvent(dialog, 1, ""));

            dialog.getAccountCombo().setSelectedItem(USER);
            assertEquals(
                    new IssueData(
                            Set.of("question", "wontfix", "bug", "documentation", "invalid", "help wanted", "duplicate", "enhancement", "good first issue"),
                            Set.of(new User("IdanKoblik", 78589468)),
                            Set.of(new Milestone("1", "gsdf"))
                    ), dialog.getData()
            );
        });
    }
}
