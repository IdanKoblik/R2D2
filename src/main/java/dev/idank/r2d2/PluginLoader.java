package dev.idank.r2d2;

import com.intellij.openapi.project.Project;
import dev.idank.r2d2.git.*;
import dev.idank.r2d2.git.data.*;
import dev.idank.r2d2.git.api.GitlabService;
import dev.idank.r2d2.managers.UserManager;
import dev.idank.r2d2.managers.GitManager;
import org.jetbrains.annotations.TestOnly;
import org.jetbrains.plugins.github.authentication.GHAccountsUtil;
import org.jetbrains.plugins.github.authentication.accounts.GithubAccount;
import org.jetbrains.plugins.gitlab.authentication.accounts.GitLabAccount;
import org.jetbrains.plugins.gitlab.authentication.accounts.PersistentGitLabAccountManager;

import java.util.*;

import static dev.idank.r2d2.dialogs.CreateIssueDialog.NO_USER;

public class PluginLoader {

    private static PluginLoader instance;

    private final GitManager gitManager =  GitManager.getInstance();
    private final Map<UserData, IssueData> issueData = new HashMap<>();

    private Vector<String> users = new Vector<>();
    private Project project;

    public static PluginLoader getInstance() {
        return instance == null ? (instance = new PluginLoader()) : instance;
    }

    public void loadIssueData() {
        if (this.project == null)
            return;

        GitManager.getInstance().loadNamespaces(project);
        GitUserExtractor userExtractor = GitUserExtractor.Companion.getInstance();
        userExtractor.invalidateCache();

        Vector<String> gitLabAccounts = getGitlabAccounts(null);
        if (!gitLabAccounts.isEmpty()) {
            for (String account : gitLabAccounts) {
                Map<Platform, UserData> users = userExtractor.extractUsers(project, createGitUser(account), Platform.GITLAB, true);
                UserData data = users.get(Platform.GITLAB);
                issueData.put(data, new GitlabService(data).fetchIssueData());
            }

            return;
        }

        Vector<String> gitHubAccounts = getGitHubAccounts(null);
        if (!gitHubAccounts.isEmpty()) {
            for (String account : gitHubAccounts) {
                Map<Platform, UserData> users = userExtractor.extractUsers(project, createGitUser(account), Platform.GITHUB, true);
                UserData data = users.get(Platform.GITLAB);
                issueData.put(data, new GitlabService(data).fetchIssueData());
            }

            return;
        }
    }

    public Map<UserData, IssueData> getIssueData() {
        return issueData;
    }

    public Vector<String> getGitAccounts() {
        if (users.isEmpty())
            users = getGitAccounts(null, null);

        return users;
    }

    @TestOnly
    public Vector<String> getGitAccounts(GithubAccount ghAccount, GitLabAccount glAccount) {
        Vector<String> accounts = new Vector<>();
        Vector<String> githubAccounts = getGitHubAccounts(ghAccount);
        Vector<String> gitLabAccounts = getGitlabAccounts(glAccount);

        accounts.addAll(githubAccounts);
        accounts.addAll(gitLabAccounts);
        return new Vector<>(Collections.unmodifiableList(accounts));
    }

    private Vector<String> getGitHubAccounts(GithubAccount account) {
        Vector<String> accounts = new Vector<>();
        if (account == null) {
            for (GithubAccount ghAccount : GHAccountsUtil.getAccounts())
                constructGithubAccount(ghAccount, accounts);

            return accounts;
        }

        constructGithubAccount(account, accounts);
        return accounts;
    }

    private void constructGithubAccount(GithubAccount githubAccount, Vector<String> accounts) {
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

    private Vector<String> getGitlabAccounts(GitLabAccount account) {
        Vector<String> accounts = new Vector<>();
        if (account == null) {
            PersistentGitLabAccountManager accountManager = new PersistentGitLabAccountManager();
            for (GitLabAccount gitlabAccount : accountManager.getAccountsState().getValue())
                constructGitlabAccount(gitlabAccount, accounts);

            return accounts;
        }

        constructGitlabAccount(account, accounts);
        return accounts;
    }

    private void constructGitlabAccount(GitLabAccount gitlabAccount, Vector<String> accounts) {
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
