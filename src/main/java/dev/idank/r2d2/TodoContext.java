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
package dev.idank.r2d2;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.javadoc.PsiDocComment;

public class TodoContext {

    private final PsiElement element;
    private final Document document;
    private final PsiFile file;
    private final Project project;

    private TodoContext(PsiElement element, Document document, PsiFile file, Project project) {
        this.element = element;
        this.document = document;
        this.file = file;
        this.project = project;
    }

    public static TodoContext create(PsiElement element) {
        PsiFile file = element.getContainingFile();
        if (file == null)
            return null;

        Document document = file.getViewProvider().getDocument();
        if (document == null)
            return null;

        if (element instanceof PsiComment && element.getParent() instanceof PsiDocComment)
            return null;

        return new TodoContext(element, document, file, element.getProject());
    }

    public boolean isValid() {
        return document != null && file != null && project != null;
    }

    public PsiElement getElement() {
        return element;
    }

    public Document getDocument() {
        return document;
    }

    public PsiFile getFile() {
        return file;
    }

    public Project getProject() {
        return project;
    }
}