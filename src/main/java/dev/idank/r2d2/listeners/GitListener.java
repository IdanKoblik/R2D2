package dev.idank.r2d2.listeners;

import dev.idank.r2d2.PluginLoader;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryChangeListener;
import org.jetbrains.annotations.NotNull;

public class GitListener implements GitRepositoryChangeListener {

    @Override
    public void repositoryChanged(@NotNull GitRepository gitRepository) {
        PluginLoader instance = PluginLoader.getInstance();
        instance.restart(gitRepository.getProject());
    }
}
