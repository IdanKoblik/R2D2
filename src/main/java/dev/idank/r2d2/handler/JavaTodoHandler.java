package dev.idank.r2d2.handler;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.impl.search.PsiTodoSearchHelperImpl;
import com.intellij.psi.search.TodoItem;
import dev.idank.r2d2.TodoContext;
import dev.idank.r2d2.TodoData;
import dev.idank.r2d2.TodoDescriptionBuilder;
import dev.idank.r2d2.actions.CreateIssueIntentionAction;

import java.util.Optional;

import static dev.idank.r2d2.TodoDescriptionBuilder.*;

public class JavaTodoHandler {

    private final TodoContext context;
    private static int lineNum = -1;

    public JavaTodoHandler(TodoContext context) {
        this.context = context;
    }

    public void process(AnnotationHolder holder) {
        TodoItem[] todos = new PsiTodoSearchHelperImpl(context.getProject())
                .findTodoItems(context.getFile());

        for (TodoItem todo : todos)
            processTodoItem(todo, holder);
    }

    private void processTodoItem(TodoItem todo, AnnotationHolder holder) {
        String todoText = getTodoText(todo);

        lineNum = context.getDocument().getLineNumber(todo.getTextRange().getStartOffset());
        String fullLine = getFullLine(lineNum);
        Optional<TodoData> todoData = parseTodoData(todoText, fullLine, lineNum);

        todoData.ifPresent(data -> createAnnotation(data, todo, holder));
    }

    private String getTodoText(TodoItem todo) {
        TextRange range = todo.getTextRange();
        return context.getDocument().getText(new TextRange(range.getStartOffset(), range.getEndOffset())).trim();
    }

    private String getFullLine(int lineNum) {
        int start = context.getDocument().getLineStartOffset(lineNum);
        int end = context.getDocument().getLineEndOffset(lineNum);
        return context.getDocument().getText(new TextRange(start, end)).trim();
    }

    private Optional<TodoData> parseTodoData(String todoText, String fullLine, int lineNum) {
        String normalTodo = todoText.replaceAll("\\s+", " ");
        int firstSpaceIndex = normalTodo.indexOf(" ");

        if (firstSpaceIndex == -1)
            return Optional.empty();

        String title = normalTodo.substring(firstSpaceIndex + 1);
        String description;

        TodoDescriptionBuilder builder = new TodoDescriptionBuilder(context.getDocument(), lineNum);
        if (fullLine.startsWith(COMMENT_PREFIX))
            description = builder.extractNormalTodoDescription(context.getProject(), context.getFile());
        else if (isValidBlockComment(fullLine, lineNum, builder))
            description = builder.extractBulkTodoDescription(context.getProject(), context.getFile());
        else
            return Optional.empty();

        return Optional.of(new TodoData(title, description));
    }

    private boolean isValidBlockComment(String fullLine, int lineNum, TodoDescriptionBuilder builder) {
        if (fullLine.startsWith(BLOCK_COMMENT_START))
            return !fullLine.startsWith(JAVADOC_START);

        if (builder.isTodo(builder.getLineRange(fullLine), context.getProject(), context.getFile()))
            return true;

        if (fullLine.trim().startsWith(COMMENT_ASTERISK))
            return !isPartOfJavadoc(lineNum);

        return false;
    }

    private boolean isPartOfJavadoc(int lineNum) {
        int maxLinesToCheck = Math.min(lineNum, 100);

        for (int i = lineNum - 1; i >= lineNum - maxLinesToCheck; i--) {
            String line = getLine(i).trim();

            if (line.startsWith(BLOCK_COMMENT_START))
                return line.startsWith(JAVADOC_START);

            if (!line.startsWith(COMMENT_ASTERISK) && !line.isEmpty())
                return false;
        }

        return false;
    }

    private String getLine(int lineNum) {
        int start = context.getDocument().getLineStartOffset(lineNum);
        int end = context.getDocument().getLineEndOffset(lineNum);
        return context.getDocument().getText(new TextRange(start, end));
    }

    private void createAnnotation(TodoData data, TodoItem todo, AnnotationHolder holder) {
        TextRange todoRange = todo.getTextRange();
        if (context.getElement().getTextRange().intersects(todoRange)) {
            TextRange intersection = todoRange.intersection(context.getElement().getTextRange());
            if (intersection != null)
                createTodoAnnotation(data.title(), data.description(), holder, intersection);
        }
    }

    /* package-private */ static void createTodoAnnotation(String title, String description, AnnotationHolder holder, TextRange range) {
        holder.newAnnotation(HighlightSeverity.INFORMATION, "Create issue")
                .range(range)
                .highlightType(ProblemHighlightType.INFORMATION)
                .withFix(new CreateIssueIntentionAction(title, description, lineNum))
                .create();
    }
}