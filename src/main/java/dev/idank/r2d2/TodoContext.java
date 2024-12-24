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