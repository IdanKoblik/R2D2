/*
 * MIT License
 *
 * Copyright (c) 2025 Idan Koblik
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
