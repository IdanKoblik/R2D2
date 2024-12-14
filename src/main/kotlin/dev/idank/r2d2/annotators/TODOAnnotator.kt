package dev.idank.r2d2.annotators

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import dev.idank.r2d2.actions.CreateIssueIntentionAction

val PREFIX: String = "//TODO"
val SEPARATOR: String = " "

class TODOAnnotator : Annotator {

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        val text = element.text
        if (text == null || !text.startsWith(PREFIX))
            return

        val prefixRange: TextRange = TextRange.from(element.textRange.startOffset, PREFIX.length)
        val separatorRange: TextRange = TextRange.from(prefixRange.endOffset, SEPARATOR.length)
        val keyRange = TextRange(separatorRange.endOffset, element.textRange.endOffset)

        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
            .range(prefixRange)
            .textAttributes(DefaultLanguageHighlighterColors.KEYWORD)
            .create()

        holder.newAnnotation(HighlightSeverity.INFORMATION, "Create issue")
            .range(keyRange)
            .highlightType(ProblemHighlightType.INFORMATION)
            .withFix(CreateIssueIntentionAction(element.text.substring(keyRange.startOffset - element.textRange.startOffset)))
            .create()
    }

}