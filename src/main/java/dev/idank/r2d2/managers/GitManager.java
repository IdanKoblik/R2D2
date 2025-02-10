package dev.idank.r2d2.managers;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.intellij.openapi.project.Project;
import dev.idank.r2d2.git.NetPattern;
import dev.idank.r2d2.git.data.GitProjectInfo;
import git4idea.repo.GitRemote;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryManager;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class GitManager {

    private final SetMultimap<String, GitProjectInfo> namespaces = HashMultimap.create();

    public void loadNamespaces(Project project) {
        GitRepositoryManager gitRepositoryManager = GitRepositoryManager.getInstance(project);

        List<GitRepository> repositories = gitRepositoryManager.getRepositories();
        for (GitRepository repository : repositories)
            loadNamespaces(repository);
    }

    public void loadNamespaces(GitRepository repo) {
        for (GitRemote remote : repo.getInfo().getRemotes())
            remote.getUrls().forEach(this::addNamespace);
    }

    public void addNamespace(String url) {
        NetPattern netPattern = NetPattern.getNetPattern(url);
        if (netPattern != null)
            addNamespaceWithPattern(netPattern.getPattern(), url);
    }

    private void addNamespaceWithPattern(String pattern, String url) {
        String host = url.replaceAll(pattern, "$1");
        String namespace = url.replaceAll(pattern, "$2");

        namespaces.put(host, new GitProjectInfo(namespace, url));
    }


    public Set<GitProjectInfo> getNamespace(String host) {
        Set<GitProjectInfo> gitProjectInfos = namespaces.get(host);
        if (gitProjectInfos.isEmpty())
            return Collections.emptySet();

        return Collections.unmodifiableSet(gitProjectInfos);
    }
}
