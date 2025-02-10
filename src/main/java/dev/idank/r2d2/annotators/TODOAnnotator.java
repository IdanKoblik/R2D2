/*
MIT License

Copyright (c) 2025 Idan Koblik

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */
package dev.idank.r2d2.annotators;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiElement;
import com.intellij.psi.javadoc.PsiDocComment;
import dev.idank.r2d2.TodoContext;
import dev.idank.r2d2.handler.JavaTodoHandler;
import dev.idank.r2d2.handler.KotlinTodoHandler;
import dev.idank.r2d2.services.PluginLoaderService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.psi.KtCallExpression;

public class TODOAnnotator implements Annotator {

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        Project project = element.getProject();
        String projectBaseDir = project.getBasePath();
        if (projectBaseDir == null || projectBaseDir.isEmpty()) {
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

        new JavaTodoHandler(context, project.getService(PluginLoaderService.class).getPluginLoader()).process(holder);
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