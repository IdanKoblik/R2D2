package dev.idank.r2d2.handler;

import com.intellij.lang.annotation.AnnotationHolder;
import org.jetbrains.kotlin.psi.KtCallExpression;
import org.jetbrains.kotlin.psi.KtNameReferenceExpression;
import org.jetbrains.kotlin.psi.KtValueArgument;

import java.util.Arrays;
import java.util.List;

import static dev.idank.r2d2.TodoDescriptionBuilder.TODO_KEYWORD;
import static dev.idank.r2d2.handler.JavaTodoHandler.createTodoAnnotation;

public class KotlinTodoHandler {

    public void handle(KtCallExpression element, AnnotationHolder holder) {
        if (!(element.getCalleeExpression() instanceof KtNameReferenceExpression nameRef))
            return;

        if (!nameRef.getReferencedName().equals(TODO_KEYWORD))
            return;

        List<KtValueArgument> args = element.getValueArguments();
        if (args.isEmpty() || args.get(0) == null)
            return;

        String[] todoArgs = args.get(0).getText().trim().split("\n");
        String title = todoArgs[0] != null ? todoArgs[0] : "No title";
        String description = String.join("\n", Arrays.copyOfRange(todoArgs, 1, todoArgs.length));

        createTodoAnnotation(title, description, holder, element.getTextRange());
    }
}
