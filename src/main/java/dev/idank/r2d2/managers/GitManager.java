/*
 * MIT License
 *
 * Copyright (c) 2025 Idan Koblik
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package dev.idank.r2d2.managers;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import dev.idank.r2d2.git.NetPattern;
import dev.idank.r2d2.git.data.GitProjectInfo;
import git4idea.repo.GitRemote;
import git4idea.repo.GitRepository;

import java.util.Collections;
import java.util.Set;

public class GitManager {

    private final SetMultimap<String, GitProjectInfo> namespaces = HashMultimap.create();

    public void loadNamespaces(GitRepository repo) {
        for (GitRemote remote : repo.getRemotes())
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
