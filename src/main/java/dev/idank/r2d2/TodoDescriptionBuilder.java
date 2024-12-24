package dev.idank.r2d2;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.search.PsiTodoSearchHelperImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TodoDescriptionBuilder {

    public static final String TODO_KEYWORD = "TODO";
    public static final String COMMENT_PREFIX = "//";
    public static final String JAVADOC_START = "/**";
    public static final String BLOCK_COMMENT_START = "/*";
    public static final String COMMENT_ASTERISK = "*";

    private final Document document;
    private final int startLine;

    public TodoDescriptionBuilder(Document document, int startLine) {
        this.document = document;
        this.startLine = startLine;
    }

    public String buildNormalTodoDescription() {
        return buildDescription(
                line -> line.startsWith(COMMENT_PREFIX),
                line -> line.replaceFirst("^//\\s?", "").trim()
        );
    }

    public String buildBulkTodoDescription(Project project, PsiFile file) {
        return buildDescription(
                line -> !line.contains("*/") && !isTodo(getLineRange(line), project, file),
                String::trim
        );
    }

    private String buildDescription(LineValidator validator, LineTransformer transformer) {
        List<String> lines = new ArrayList<>();

        for (int i = startLine + 1; i < document.getLineCount(); i++) {
            String line = getLine(i);
            if (line.isBlank() || line.contains("\n\n") || !validator.isValid(line))
                break;

            lines.add(transformer.transform(line));
        }

        return String.join("\n", lines);
    }

    private String getLine(int lineNum) {
        TextRange range = new TextRange(
                document.getLineStartOffset(lineNum),
                document.getLineEndOffset(lineNum)
        );
        return document.getText(range);
    }

    private TextRange getLineRange(String line) {
        int lineNum = document.getLineNumber(document.getText().indexOf(line));
        return new TextRange(
                document.getLineStartOffset(lineNum),
                document.getLineEndOffset(lineNum)
        );
    }

    private boolean isTodo(TextRange textRange, Project project, PsiFile file) {
        return Arrays.stream(new PsiTodoSearchHelperImpl(project).findTodoItems(file))
                .anyMatch(todo -> todo.getTextRange().equals(textRange));
    }
}