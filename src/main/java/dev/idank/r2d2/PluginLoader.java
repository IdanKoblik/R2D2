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
package dev.idank.r2d2;

import com.intellij.openapi.project.Project;
import dev.idank.r2d2.git.GitHost;
import dev.idank.r2d2.git.GitHostFactory;
import dev.idank.r2d2.git.Platform;
import dev.idank.r2d2.git.data.AuthData;
import dev.idank.r2d2.git.data.GitProjectInfo;
import dev.idank.r2d2.git.data.GitUser;
import dev.idank.r2d2.git.data.issue.IssueData;
import dev.idank.r2d2.managers.GitManager;
import dev.idank.r2d2.managers.UserManager;
import org.jetbrains.plugins.github.authentication.GHAccountsUtil;
import org.jetbrains.plugins.github.authentication.accounts.GithubAccount;
import org.jetbrains.plugins.gitlab.authentication.accounts.GitLabAccount;
import org.jetbrains.plugins.gitlab.authentication.accounts.PersistentGitLabAccountManager;

import java.util.*;

import static dev.idank.r2d2.dialogs.CreateIssueDialog.NO_USER;

public class PluginLoader {

    public static final String SSH_REGEX = "git@([a-zA-Z0-9.-]+):(.*?)(\\.git)?$";
    public static final String HTTPS_REGEX = "https://([a-zA-Z0-9.-]+)/(.+?)(\\.git)?$";
    public static final String HTTP_REGEX = "http://([a-zA-Z0-9.-]+)/(.+?)(\\.git)?$";

    private final Map<AuthData, IssueData> issueData = new HashMap<>();
    private GitManager gitManager;
    private UserManager userManager;

    public PluginLoader(Project project) {
        onEnable(project);
    }

    public void onEnable(Project project) {
        this.gitManager = new GitManager();
        this.userManager = new UserManager();
        this.gitManager.loadNamespaces(project);

        Set<String> gitAccounts = getGitAccounts();

        for (String account : gitAccounts) {
            GitHostFactory factory = new GitHostFactory();
            GitUser gitUser = createGitUser(account);
            GitHost<?> gitHost = factory.createGitHost(project, gitUser);
            gitHost.getAuthData().ifPresent(data -> {
                this.userManager.addUserData(gitUser, data);

                try {
                    issueData.put(data, gitHost.fetchIssueData());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public GitManager getGitManager() {
        return gitManager;
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public Map<AuthData, IssueData> getIssueData() {
        return Collections.unmodifiableMap(issueData);
    }

    public Set<String> getGitAccounts() {
        Set<String> users = getGitAccountsHelper();
        return Collections.unmodifiableSet(users);
    }

    private Set<String> getGitAccountsHelper() {
        Set<String> accounts = new HashSet<>();
        Set<String> githubAccounts = getGitHubAccounts();
        Set<String> gitLabAccounts = getGitlabAccounts();

        accounts.addAll(githubAccounts);
        accounts.addAll(gitLabAccounts);
        return accounts;
    }

    private Set<String> getGitHubAccounts() {
        Set<String> accounts = new HashSet<>();
        for (GithubAccount ghAccount : GHAccountsUtil.getAccounts())
            constructGithubAccount(ghAccount, accounts);

        return accounts;
    }

    private void constructGithubAccount(GithubAccount githubAccount, Set<String> accounts) {
        String serverUrl = githubAccount.getServer().toString();
        Set<GitProjectInfo> infos = this.gitManager.getNamespace(serverUrl);
        if (infos.isEmpty())
            return;

        for (GitProjectInfo info : infos) {
            GitUser account = new GitUser(
                    githubAccount.getName(),
                    githubAccount.getServer().getSchema() + "://" + githubAccount.getServer(),
                    new GitProjectInfo(info.namespace(), info.url()),
                    Platform.GITHUB
            );

            this.userManager.addUser(account);
            accounts.add(account.toString());
        }
    }

    private Set<String> getGitlabAccounts() {
        Set<String> accounts = new HashSet<>();
        PersistentGitLabAccountManager accountManager = new PersistentGitLabAccountManager();
        for (GitLabAccount gitlabAccount : accountManager.getAccountsState().getValue())
            constructGitlabAccount(gitlabAccount, accounts);

        return accounts;
    }

    private void constructGitlabAccount(GitLabAccount gitlabAccount, Set<String> accounts) {
        String server = gitlabAccount.getServer().toString();
        String cleanServer = server.replace("https://", "").replace("http://", "");
        Set<GitProjectInfo> infos = this.gitManager.getNamespace(cleanServer);
        if (infos.isEmpty())
            return;

        for (GitProjectInfo info : infos) {
            GitUser account = new GitUser(
                    gitlabAccount.getName(),
                    server,
                    new GitProjectInfo(info.namespace(), info.url()),
                    Platform.GITLAB
            );

            this.userManager.addUser(account);
            accounts.add(account.toString());
        }
    }

    public GitUser createGitUser(String selectedAccount) {
        if (selectedAccount.equals(NO_USER))
            return null;

        Optional<GitUser> userOpt = this.userManager.getUser(selectedAccount);
        return userOpt.orElse(null);
    }
}
