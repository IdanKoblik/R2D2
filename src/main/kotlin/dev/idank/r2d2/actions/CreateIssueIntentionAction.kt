package dev.idank.r2d2.actions

import com.intellij.codeInsight.intention.impl.BaseIntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import dev.idank.r2d2.dialogs.CreateIssueDialog
import javax.swing.SwingUtilities

private const val NAME: String = "Create issue"

class CreateIssueIntentionAction(private val title: String, private val description: String?) : BaseIntentionAction() {

    override fun getText(): String {
        return NAME
    }

    override fun getFamilyName(): String {
        return NAME
    }

    override fun isAvailable(project: Project, editor: Editor?, file: PsiFile?): Boolean {
        return true
    }

    override fun invoke(project: Project, editor: Editor?, file: PsiFile?) {
        if (editor != null && file != null) {
            val dialog = CreateIssueDialog(project, title, description)
            dialog.show()
        }
    }


}