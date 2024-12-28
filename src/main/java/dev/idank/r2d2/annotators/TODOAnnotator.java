package dev.idank.r2d2.annotators;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.javadoc.PsiDocComment;
import dev.idank.r2d2.TodoContext;
import dev.idank.r2d2.handler.JavaTodoHandler;
import dev.idank.r2d2.handler.KotlinTodoHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.psi.KtCallExpression;

public class TODOAnnotator implements Annotator {

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        Project project = element.getProject();
        VirtualFile projectBaseDir = project.getBaseDir();
        if (projectBaseDir == null) {
            Messages.showMessageDialog("Could not find the project base directory.", "Error", Messages.getErrorIcon());
            return;
        }

        if (isInJavaDoc(element))
            return;

        TodoContext context = TodoContext.create(element);
        if (context != null && !context.isValid())
            return;

        if (element instanceof KtCallExpression expression) {
            new KotlinTodoHandler().handle(expression, holder);
            return;
        }

        new JavaTodoHandler(context).process(holder);
    }

    private boolean isInJavaDoc(PsiElement element) {
        PsiElement parent = element.getParent();
        while (parent != null) {
            if (parent instanceof PsiDocComment)
                return true;

            parent = parent.getParent();
        }

        return false;
    }

}