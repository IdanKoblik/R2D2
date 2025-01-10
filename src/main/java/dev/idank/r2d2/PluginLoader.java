package dev.idank.r2d2;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import dev.idank.r2d2.git.*;
import dev.idank.r2d2.git.data.GitInfo;
import dev.idank.r2d2.git.data.GitUser;
import dev.idank.r2d2.git.data.IssueData;
import dev.idank.r2d2.git.data.UserData;
import dev.idank.r2d2.git.api.GithubService;
import dev.idank.r2d2.git.api.GitlabService;
import dev.idank.r2d2.utils.GitUtils;
import org.jetbrains.annotations.TestOnly;
import org.jetbrains.annotations.VisibleForTesting;
import org.jetbrains.plugins.github.authentication.GHAccountsUtil;
import org.jetbrains.plugins.github.authentication.accounts.GithubAccount;
import org.jetbrains.plugins.gitlab.authentication.accounts.GitLabAccount;
import org.jetbrains.plugins.gitlab.authentication.accounts.PersistentGitLabAccountManager;

import java.util.Collections;
import java.util.Map;
import java.util.Vector;

import static dev.idank.r2d2.dialogs.CreateIssueDialog.NO_USER;

public class PluginLoader {

    private static PluginLoader instance;
    private IssueData issueData;
    private Project project;

    public static PluginLoader getInstance() {
        return instance == null ? (instance = new PluginLoader()) : instance;
    }

    public void loadIssueData() {
        if (this.project == null)
            return;

        if (getGitAccounts().isEmpty())
            return;

        GitUserExtractor userExtractor = GitUserExtractor.Companion.getInstance();
        userExtractor.invalidateCache();

        Vector<String> gitLabAccounts = getGitLabAccounts(null, null);
        if (!gitLabAccounts.isEmpty()) {
            Map<Platform, UserData> users = userExtractor.extractUsers(project, createGitUser(gitLabAccounts.get(0)), Platform.GITLAB);
            this.issueData = new GitlabService(users.get(Platform.GITLAB)).fetchIssueData();
            return;
        }

        Vector<String> gitHubAccounts = getGitHubAccounts(null, null);
        if (!gitHubAccounts.isEmpty()) {
            Map<Platform, UserData> users = userExtractor.extractUsers(project, createGitUser(gitHubAccounts.get(0)), Platform.GITHUB);
            this.issueData = new GithubService(users.get(Platform.GITHUB)).fetchIssueData();
            return;
        }
    }

    public IssueData getIssueData() {
        return issueData;
    }

    public Vector<String> getGitAccounts() {
        return getGitAccounts(null, null, null);
    }

    @TestOnly
    public Vector<String> getGitAccounts(GithubAccount ghAccount, GitLabAccount glAccount, String repoPath) {
        Vector<String> accounts = new Vector<>();
        Vector<String> githubAccounts = getGitHubAccounts(ghAccount, repoPath);
        Vector<String> gitLabAccounts = getGitLabAccounts(glAccount, repoPath);

        accounts.addAll(githubAccounts);
        accounts.addAll(gitLabAccounts);
        return new Vector<>(Collections.unmodifiableList(accounts));
    }

    private Vector<String> getGitHubAccounts(GithubAccount account, String repoPath) {
        Vector<String> accounts = new Vector<>();

        if (account == null) {
            for (GithubAccount ghAccount : GHAccountsUtil.getAccounts()) {
                String serverUrl = ghAccount.getServer().toString();
                if (GitUtils.extractGitInfo(ProjectManager.getInstance().getOpenProjects()[0].getBasePath())
                        .containsKey(serverUrl)) {
                    accounts.add(ghAccount.getName() + " / " + ghAccount.getServer().getSchema() +"://" + ghAccount.getServer() + " / " + Platform.GITHUB.getName());
                }
            }

            return accounts;
        }

        String serverUrl = account.getServer().toString();
        if (GitUtils.extractGitInfo(repoPath)
                .containsKey(serverUrl)) {
            accounts.add(account.getName() + " / " + account.getServer().getSchema() +"://" + account.getServer() + " / " + Platform.GITHUB.getName());
        }

        return accounts;
    }

    private Vector<String> getGitLabAccounts(GitLabAccount account, String repoPath) {
        Vector<String> accounts = new Vector<>();
        if (account == null) {
            PersistentGitLabAccountManager accountManager = new PersistentGitLabAccountManager();
            for (GitLabAccount gitLabAccount : accountManager.getAccountsState().getValue()) {
                String server = gitLabAccount.getServer().toString();
                String cleanServer = server.replace("https://", "").replace("http://", "");

                if (GitUtils.extractGitInfo(ProjectManager.getInstance().getOpenProjects()[0].getBasePath())
                        .containsKey(cleanServer)) {
                    accounts.add(gitLabAccount.getName() + " / " + server + " / " + Platform.GITLAB.getName());
                }
            }

            return accounts;
        }

        String server = account.getServer().toString();
        String cleanServer = server.replace("https://", "").replace("http://", "");

        if (GitUtils.extractGitInfo(repoPath)
                .containsKey(cleanServer)) {
            accounts.add(account.getName() + " / " + server + " / " + Platform.GITLAB.getName());
        }

        return accounts;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public GitUser createGitUser(String selectedAccount) {
        return createGitUser(selectedAccount, ProjectManager.getInstance().getOpenProjects()[0].getBasePath());
    }

    @TestOnly
    public GitUser createGitUser(String selectedAccount, String repoPath) {
        if (selectedAccount.equals(NO_USER))
            return null;

        String[] account = selectedAccount.split(" / ");
        GitInfo gitInfo = GitUtils.extractGitInfo(repoPath).get(
                account[1].replace("https://", "").replace("http://", "")
        );

        return new GitUser(account[0], account[1], gitInfo.namespace(), gitInfo.url(), Platform.fromName(account[2]));
    }
}
