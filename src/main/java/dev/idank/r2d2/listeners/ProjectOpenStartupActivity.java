package dev.idank.r2d2.listeners;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.ProjectActivity;
import dev.idank.r2d2.PluginLoader;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ProjectOpenStartupActivity implements ProjectActivity {

    @Override
    public @Nullable Object execute(@NotNull Project project, @NotNull Continuation<? super Unit> continuation) {
        PluginLoader instance = PluginLoader.getInstance();
        instance.setProject(project);
        instance.loadIssueData();

        return null;
    }
}
