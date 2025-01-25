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
package dev.idank.r2d2.managers;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import dev.idank.r2d2.git.api.NetPattern;
import dev.idank.r2d2.git.data.GitInfo;
import git4idea.repo.GitRemote;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryManager;

import java.util.*;

public class GitManager {

    private final Map<String, Set<GitInfo>> namespaces = new HashMap<>();

    private static GitManager instance;

    public static GitManager getInstance() {
        return instance == null ? (instance = new GitManager()) : instance;
    }

    public void loadNamespaces(Project project) {
        if (ApplicationManager.getApplication().isUnitTestMode())
            return;

        GitRepositoryManager gitRepositoryManager = GitRepositoryManager.getInstance(project);

        List<GitRepository> repositories = gitRepositoryManager.getRepositories();
        for (GitRepository repository : repositories)
            loadNamespaces(repository);
    }

    public boolean loadNamespaces(GitRepository repo) {
        for (GitRemote remote : repo.getInfo().getRemotes())
            remote.getUrls().forEach(this::addNamespace);

        return true;
    }

    public void addNamespace(String url) {
        NetPattern netPattern = NetPattern.getNetPattern(url);
        if (netPattern != null)
            extract(netPattern.getPattern(), url);
    }

    private void extract(String pattern, String url) {
        String host = url.replaceAll(pattern, "$1");
        String namespace = url.replaceAll(pattern, "$2");
        this.namespaces.computeIfAbsent(host, infos -> new HashSet<>()).add(new GitInfo(namespace, url));
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
