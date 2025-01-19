package dev.idank.r2d2.listeners;

import com.intellij.openapi.application.ApplicationManager;
import dev.idank.r2d2.PluginLoader;
import dev.idank.r2d2.git.GitUserExtractor;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryChangeListener;
import org.jetbrains.annotations.NotNull;

public class GitListener implements GitRepositoryChangeListener {

    @Override
    public void repositoryChanged(@NotNull GitRepository gitRepository) {
        GitUserExtractor gitUserExtractor = GitUserExtractor.Companion.getInstance();
        PluginLoader instance = PluginLoader.getInstance();

        ApplicationManager.getApplication().runWriteAction(() -> {
            instance.setProject(gitRepository.getProject());
            gitUserExtractor.invalidateCache();

            if (!ApplicationManager.getApplication().isUnitTestMode())
                instance.loadIssueData();
        });
    }
}
