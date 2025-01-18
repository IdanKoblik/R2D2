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
package dev.idank.r2d2.handler;

import com.intellij.lang.annotation.AnnotationHolder;
import org.jetbrains.kotlin.psi.KtCallExpression;
import org.jetbrains.kotlin.psi.KtNameReferenceExpression;
import org.jetbrains.kotlin.psi.KtValueArgument;

import java.util.Arrays;
import java.util.List;

import static dev.idank.r2d2.handler.JavaTodoHandler.createTodoAnnotation;

public class KotlinTodoHandler {

    public void handle(KtCallExpression element, AnnotationHolder holder) {
        if (!(element.getCalleeExpression() instanceof KtNameReferenceExpression nameRef))
            return;

        if (!nameRef.getReferencedName().equals("TODO"))
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