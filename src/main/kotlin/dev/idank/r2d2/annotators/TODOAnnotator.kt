package dev.idank.r2d2.annotators

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.Document
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.search.PsiTodoSearchHelperImpl
import dev.idank.r2d2.actions.CreateIssueIntentionAction

class TODOAnnotator : Annotator {

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        val containingFile = element.containingFile ?: return
        val todos = PsiTodoSearchHelperImpl(element.project).findTodoItems(element.containingFile)

        todos.forEach { todoItem ->
            val document = containingFile.viewProvider.document ?: return@forEach

            val lineNumber = document.getLineNumber(todoItem.textRange.startOffset)
            val lineStartOffset = document.getLineStartOffset(lineNumber)
            val lineEndOffset = document.getLineEndOffset(lineNumber)

            val todoRange = TextRange(lineStartOffset, lineEndOffset)
            val elementRange = element.textRange
            if (todoRange.startOffset < elementRange.startOffset || todoRange.endOffset > elementRange.endOffset) {
                return@forEach
            }

            val fullLineText = document.getText(todoRange).trim()

            var description: String = ""
            if (fullLineText.startsWith("//"))
                description = handleNormalTodo(document, lineNumber)
            else if (fullLineText.trim().startsWith("/*") || fullLineText.startsWith("TODO") || fullLineText.trim().startsWith("*"))
                description = handleBulkTodo(document, lineNumber)

            val todoText: String = fullLineText.substring(
                todoItem.textRange.startOffset - lineStartOffset,
                todoItem.textRange.endOffset - lineStartOffset
            ).replace("TODO", "").replace(":", "").trim()

            holder.newAnnotation(HighlightSeverity.INFORMATION, "Create issue")
                .range(todoRange)
                .highlightType(ProblemHighlightType.INFORMATION)
                .withFix(CreateIssueIntentionAction(
                    todoText,
                    description
                ))
                .create()
        }
    }

    private fun handleNormalTodo(document: Document, todoLine: Int): String {
        val lines = mutableListOf<String>()

        for (i in (todoLine + 1) until document.lineCount) {
            val start = document.getLineStartOffset(i)
            val end = document.getLineEndOffset(i)
            val range = TextRange(start, end)

            val line = document.getText(range)
            if (!line.startsWith("//"))
                break

            if (line.contains("\n\n") || line.isBlank())
                break

            lines.add(
                line.removePrefix("//")
                    .removePrefix("// ")
                    .trim()
            )
        }

        return lines.joinToString("\n")
    }

    private fun handleBulkTodo(document: Document, todoLine: Int): String {
        val lines = mutableListOf<String>()

        for (i in todoLine + 1 until document.lineCount) {
            val start = document.getLineStartOffset(i)
            val end = document.getLineEndOffset(i)
            val range = TextRange(start, end)

            val line = document.getText(range)
            if (line.contains("\n\n") || line.isBlank() || line.contains("*/"))
                break

            lines.add(line.trim())
        }

        return lines.joinToString("\n")
    }
}
