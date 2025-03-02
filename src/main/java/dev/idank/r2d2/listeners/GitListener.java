package dev.idank.r2d2.listeners;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import dev.idank.r2d2.PluginLoader;
import dev.idank.r2d2.services.PluginLoaderService;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryChangeListener;
import org.jetbrains.annotations.NotNull;

public class GitListener implements GitRepositoryChangeListener {

    private final @NotNull Project project;

    public GitListener(@NotNull Project project) {
        this.project = project;
    }

    @Override
    public void repositoryChanged(@NotNull GitRepository gitRepository) {
        ApplicationManager.getApplication().runReadAction(() -> {
            PluginLoaderService service = project.getService(PluginLoaderService.class);
            PluginLoader pluginLoader = service.getPluginLoader();
            pluginLoader.onEnable(project, gitRepository);
        });
    }
}
