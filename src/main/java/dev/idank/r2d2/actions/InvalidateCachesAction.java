package dev.idank.r2d2.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.ui.Messages;
import dev.idank.r2d2.PluginLoader;
import dev.idank.r2d2.git.GitUserExtractor;
import dev.idank.r2d2.managers.GitManager;
import org.jetbrains.annotations.NotNull;

public class InvalidateCachesAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        GitUserExtractor gitUserExtractor = GitUserExtractor.Companion.getInstance();
        PluginLoader pluginLoader = PluginLoader.getInstance();
        ApplicationManager.getApplication().runWriteAction(() -> {
            gitUserExtractor.invalidateCache();

            if (!ApplicationManager.getApplication().isUnitTestMode())
                pluginLoader.loadIssueData();
        });

        if (!ApplicationManager.getApplication().isUnitTestMode()) {
            ApplicationManager.getApplication().invokeLater(() ->
                    Messages.showInfoMessage(event.getProject(),
                            "Cache invalidated successfully!", "Cache Invalidation")
            );
        }
    }
}