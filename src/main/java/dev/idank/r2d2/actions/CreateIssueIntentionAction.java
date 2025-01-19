/*
MIT License

Copyright (c) 2025 Idan Koblik

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */
package dev.idank.r2d2.actions;

import com.intellij.codeInsight.intention.impl.BaseIntentionAction;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import dev.idank.r2d2.PluginLoader;
import dev.idank.r2d2.dialogs.CreateIssueDialog;
import dev.idank.r2d2.utils.UIUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class CreateIssueIntentionAction extends BaseIntentionAction {

    public static final String NAME = "Create issue";

    private final String title;
    private final String description;
    private final int lineNum;

    public CreateIssueIntentionAction(String title, String description, int lineNum) {
        this.title = title;
        this.description = description;
        this.lineNum = lineNum;
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return true;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        if (editor == null || file == null)
            return;

        ApplicationManager.getApplication().invokeLater(() -> {
            if (PluginLoader.getInstance().getGitAccounts().isEmpty()) {
                UIUtils.showError("You must have at least one git user connected to idea", new JTextField());
                ShowSettingsUtil.getInstance().showSettingsDialog(project,
                        Configurable.APPLICATION_CONFIGURABLE.getName());

                return;
            }

            CreateIssueDialog dialog = new CreateIssueDialog(project, title, description, lineNum, editor.getDocument());
            dialog.showAndGet();
        });
    }

    @Override
    public @NotNull @IntentionName String getText() {
        return NAME;
    }

    @Override
    public @NotNull @IntentionFamilyName String getFamilyName() {
        return NAME;
    }
}