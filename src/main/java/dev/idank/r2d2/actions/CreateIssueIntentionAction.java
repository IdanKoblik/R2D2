package dev.idank.r2d2.actions;

import com.intellij.codeInsight.intention.impl.BaseIntentionAction;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import dev.idank.r2d2.dialogs.CreateIssueDialog;
import org.jetbrains.annotations.NotNull;

public class CreateIssueIntentionAction extends BaseIntentionAction {

    public static final String NAME = "Create issue";

    private final String title;
    private final String description;

    public CreateIssueIntentionAction(String title, String description) {
        this.title = title;
        this.description = description;
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return true;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        if (editor != null && file != null) {
            CreateIssueDialog dialog = new CreateIssueDialog(project, title, description);
            dialog.show();
        }
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