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
package dev.idank.r2d2.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.ui.Messages;
import dev.idank.r2d2.PluginLoader;
import dev.idank.r2d2.git.GitUserExtractor;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class InvalidateCachesAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        PluginLoader instance = PluginLoader.getInstance();
        GitUserExtractor gitUserExtractor = GitUserExtractor.INSTANCE;

        ProgressManager.getInstance().run(new Task.Backgroundable(event.getProject(), "Invalidating Caches", false) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                if (ApplicationManager.getApplication().isUnitTestMode())
                    return;


                ApplicationManager.getApplication().runReadAction(() -> {
                    gitUserExtractor.invalidateCache();
                    instance.loadIssueData(event.getProject());
                });

                SwingUtilities.invokeLater(() ->
                        Messages.showInfoMessage(event.getProject(), "Cache invalidated successfully!", "Cache Invalidation")
                );
            }
        });
    }
}