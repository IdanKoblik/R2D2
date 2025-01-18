package dev.idank.r2d2.managers;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import dev.idank.r2d2.git.data.GitInfo;
import git4idea.repo.*;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class GitManager {

    private final Map<String, Set<GitInfo>> namespaces = new HashMap<>();
    private final String sshRegex = "git@([a-zA-Z0-9.-]+):(.*?)(\\.git)?$";
    private final String httpsRegex = "https://([a-zA-Z0-9.-]+)/(.+?)(\\.git)?$";

    private static GitManager instance;

    public static GitManager getInstance() {
        return instance == null ? (instance = new GitManager()) : instance;
    }

    public void loadNamespaces(Project project) {
        GitRepositoryManager gitRepositoryManager = GitRepositoryManager.getInstance(project);

        List<GitRepository> repositories = gitRepositoryManager.getRepositories();
        for (GitRepository repository : repositories) {
            for (GitRemote remote : repository.getInfo().getRemotes())
                remote.getUrls().forEach(this::addNamespace);

            for (GitSubmoduleInfo info : repository.getSubmodules())
                addNamespace(info.getUrl());
        }
    }

    public void addNamespace(String url) {
        if (url.matches(sshRegex)) {
            String host = url.replaceAll(sshRegex, "$1");
            String namespace = url.replaceAll(sshRegex, "$2");
            this.namespaces.computeIfAbsent(host, infos -> new HashSet<>()).add(new GitInfo(namespace, url));
        } else if (url.matches(httpsRegex)) {
            String host = url.replaceAll(httpsRegex, "$1");
            String namespace = url.replaceAll(httpsRegex, "$2");
            this.namespaces.computeIfAbsent(host, infos -> new HashSet<>()).add(new GitInfo(namespace, url));
        }
    }

    public void clear() {
        this.namespaces.clear();
    }

    public Optional<Set<GitInfo>> getNamespace(String url) {
        Set<GitInfo> gitInfos = this.namespaces.get(url);
        if (gitInfos == null || gitInfos.isEmpty())
            return Optional.empty();

        return Optional.of(gitInfos);
    }

}
