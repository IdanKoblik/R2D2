package dev.idank.r2d2.listeners;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.ProjectActivity;
import dev.idank.r2d2.PluginLoader;
import dev.idank.r2d2.git.GitUserExtractor;
import dev.idank.r2d2.managers.GitManager;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ProjectOpenStartupActivity implements ProjectActivity {

    @Override
    public @Nullable Object execute(@NotNull Project project, @NotNull Continuation<? super Unit> continuation) {
        GitUserExtractor gitUserExtractor = GitUserExtractor.Companion.getInstance();
        PluginLoader instance = PluginLoader.getInstance();
        GitManager gitManager = GitManager.getInstance();
        gitManager.clear();

        ApplicationManager.getApplication().runWriteAction(() -> {
            instance.setProject(project);
            gitUserExtractor.invalidateCache();

            if (!ApplicationManager.getApplication().isUnitTestMode())
                instance.loadIssueData();
        });

        return instance.getIssueData();
    }
}
