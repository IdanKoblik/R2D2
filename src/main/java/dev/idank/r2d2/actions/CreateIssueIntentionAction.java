package dev.idank.r2d2.actions;

import com.intellij.codeInsight.intention.impl.BaseIntentionAction;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.configurable.VcsManagerConfigurable;
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
                        VcsManagerConfigurable.APPLICATION_CONFIGURABLE.getName());

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