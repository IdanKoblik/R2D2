package dev.idank.r2d2.actions

import com.intellij.codeInsight.intention.impl.BaseIntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import dev.idank.r2d2.dialogs.CreateIssueDialog

val NAME: String = "Create issue"

class CreateIssueIntentionAction(private val issueTitle: String) : BaseIntentionAction() {

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
        val dialog = CreateIssueDialog(project, issueTitle)
        dialog.show()
    }

}