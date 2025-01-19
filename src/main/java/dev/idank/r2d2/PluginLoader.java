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
import dev.idank.r2d2.git.GitUserExtractor;
import dev.idank.r2d2.git.Platform;
import dev.idank.r2d2.git.api.GithubService;
import dev.idank.r2d2.git.api.GitlabService;
import dev.idank.r2d2.git.data.GitInfo;
import dev.idank.r2d2.git.data.GitUser;
import dev.idank.r2d2.git.data.IssueData;
import dev.idank.r2d2.git.data.UserData;
import dev.idank.r2d2.managers.GitManager;
import dev.idank.r2d2.managers.UserManager;
import org.jetbrains.annotations.TestOnly;
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

    private static PluginLoader instance;

    private final GitManager gitManager =  GitManager.getInstance();
    private final Map<UserData, IssueData> issueData = new HashMap<>();

    private Set<String> users = new HashSet<>();
    private Project project;

    public static PluginLoader getInstance() {
        return instance == null ? (instance = new PluginLoader()) : instance;
    }

    public void loadIssueData() {
        if (this.project == null)
            return;

        GitUserExtractor userExtractor = GitUserExtractor.Companion.getInstance();
        userExtractor.invalidateCache();

        this.issueData.clear();
        this.users.clear();
        GitManager.getInstance().clear();
        UserManager.getInstance().clear();

        GitManager.getInstance().loadNamespaces(project);
        Set<String> gitLabAccounts = getGitlabAccounts(null);
        if (!gitLabAccounts.isEmpty()) {
            for (String account : gitLabAccounts) {
                Map<Platform, UserData> gitlabUsers = userExtractor.extractUsers(project, createGitUser(account), Platform.GITLAB, true);
                UserData data = gitlabUsers.get(Platform.GITLAB);
                issueData.put(data, new GitlabService(data).fetchIssueData());
            }

            return;
        }

        Set<String> gitHubAccounts = getGitHubAccounts(null);
        if (!gitHubAccounts.isEmpty()) {
            for (String account : gitHubAccounts) {
                Map<Platform, UserData> githubUsers = userExtractor.extractUsers(project, createGitUser(account), Platform.GITHUB, true);
                UserData data = githubUsers.get(Platform.GITHUB);
                issueData.put(data, new GithubService(data).fetchIssueData());
            }
        }
    }

    public Map<UserData, IssueData> getIssueData() {
        return issueData;
    }

    public Set<String> getGitAccounts() {
        if (users.isEmpty())
            this.users = getGitAccountsHelper(null, null);

        return Collections.unmodifiableSet(this.users);
    }

    @TestOnly
    public Set<String> getGitAccounts(GithubAccount ghAccount, GitLabAccount glAccount) {
        return Collections.unmodifiableSet(getGitAccountsHelper(ghAccount, glAccount));
    }

    private Set<String> getGitAccountsHelper(GithubAccount ghAccount, GitLabAccount glAccount) {
        Set<String> accounts = new HashSet<>();
        Set<String> githubAccounts = getGitHubAccounts(ghAccount);
        Set<String> gitLabAccounts = getGitlabAccounts(glAccount);

        accounts.addAll(githubAccounts);
        accounts.addAll(gitLabAccounts);
        return accounts;
    }

    private Set<String> getGitHubAccounts(GithubAccount account) {
        Set<String> accounts = new HashSet<>();
        if (account == null) {
            for (GithubAccount ghAccount : GHAccountsUtil.getAccounts())
                constructGithubAccount(ghAccount, accounts);

            return accounts;
        }

        constructGithubAccount(account, accounts);
        return accounts;
    }

    private void constructGithubAccount(GithubAccount githubAccount, Set<String> accounts) {
        String serverUrl = githubAccount.getServer().toString();
        Optional<Set<GitInfo>> infosOpt = this.gitManager.getNamespace(serverUrl);
        if (infosOpt.isEmpty())
            return;

        for (GitInfo info : infosOpt.get()) {
            GitUser account = new GitUser(
                    githubAccount.getName(),
                    githubAccount.getServer().getSchema() + "://" + githubAccount.getServer(),
                    info.namespace(),
                    info.url(),
                    Platform.GITHUB
            );

            UserManager.getInstance().addUser(account);
            accounts.add(account.toString());
        }
    }

    private Set<String> getGitlabAccounts(GitLabAccount account) {
        Set<String> accounts = new HashSet<>();
        if (account == null) {
            PersistentGitLabAccountManager accountManager = new PersistentGitLabAccountManager();
            for (GitLabAccount gitlabAccount : accountManager.getAccountsState().getValue())
                constructGitlabAccount(gitlabAccount, accounts);

            return accounts;
        }

        constructGitlabAccount(account, accounts);
        return accounts;
    }

    private void constructGitlabAccount(GitLabAccount gitlabAccount, Set<String> accounts) {
        String server = gitlabAccount.getServer().toString();
        String cleanServer = server.replace("https://", "").replace("http://", "");
        Optional<Set<GitInfo>> infosOpt = this.gitManager.getNamespace(cleanServer);
        if (infosOpt.isEmpty())
            return;

        for (GitInfo info : infosOpt.get()) {
            GitUser account = new GitUser(
                    gitlabAccount.getName(),
                    server,
                    info.namespace(),
                    info.url(),
                    Platform.GITLAB
            );

            UserManager.getInstance().addUser(account);
            accounts.add(account.toString());
        }
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public GitUser createGitUser(String selectedAccount) {
        if (selectedAccount.equals(NO_USER))
            return null;

        Optional<GitUser> userOpt = UserManager.getInstance().getUser(selectedAccount);
        return userOpt.orElse(null);
    }
}
