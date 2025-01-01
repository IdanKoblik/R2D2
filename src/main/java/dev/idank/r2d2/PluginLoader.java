package dev.idank.r2d2;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.popup.IconButton;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.ui.content.AlertIcon;
import dev.idank.r2d2.git.*;
import dev.idank.r2d2.git.data.GitInfo;
import dev.idank.r2d2.git.data.GitUser;
import dev.idank.r2d2.git.data.IssueData;
import dev.idank.r2d2.git.data.UserData;
import dev.idank.r2d2.git.github.GithubService;
import dev.idank.r2d2.git.gitlab.GitlabService;
import dev.idank.r2d2.utils.GitUtils;
import org.jetbrains.plugins.github.authentication.GHAccountsUtil;
import org.jetbrains.plugins.github.authentication.accounts.GithubAccount;
import org.jetbrains.plugins.gitlab.authentication.accounts.GitLabAccount;
import org.jetbrains.plugins.gitlab.authentication.accounts.PersistentGitLabAccountManager;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import static dev.idank.r2d2.dialogs.CreateIssueDialog.NO_USER;

public class PluginLoader {

    private final IssueData emptyIssueData = new IssueData(Set.of());

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

        if (!getGitLabAccounts().isEmpty()) {
            GitUserExtractor userExtractor = GitUserExtractor.Companion.getInstance();
            userExtractor.invalidateCache();
            Map<Platform, UserData> users = userExtractor.extractUsers(project, createGitUser(getGitLabAccounts().get(0)), Platform.GITLAB);

            this.issueData = new GitlabService(users.get(Platform.GITLAB)).fetchIssueData();
            return;
        }

        if (!getGitHubAccounts().isEmpty()) {
            GitUserExtractor userExtractor = GitUserExtractor.Companion.getInstance();
            userExtractor.invalidateCache();
            Map<Platform, UserData> users = userExtractor.extractUsers(project, createGitUser(getGitHubAccounts().get(0)), Platform.GITHUB);

            this.issueData = new GithubService(users.get(Platform.GITHUB)).fetchIssueData();
            return;
        }
    }

    public IssueData getIssueData() {
        return issueData;
    }

    public Vector<String> getGitAccounts() {
        Vector<String> accounts = new Vector<>();
        Vector<String> githubAccounts = getGitHubAccounts();
        Vector<String> gitLabAccounts = getGitLabAccounts();

        accounts.addAll(githubAccounts);
        accounts.addAll(gitLabAccounts);
        return new Vector<>(Collections.unmodifiableList(accounts));
    }

    public Vector<String> getGitHubAccounts() {
        Vector<String> accounts = new Vector<>();
        if (githubAccountsSize() == 0)
            return accounts;

        for (GithubAccount account : GHAccountsUtil.getAccounts()) {
            String serverUrl = account.getServer().toString();
            if (GitUtils.extractGitInfo(ProjectManager.getInstance().getOpenProjects()[0].getBasePath())
                    .containsKey(serverUrl)) {
                accounts.add(account.getName() + " / " + account.getServer().getSchema() +"://" + account.getServer() + " / " + Platform.GITHUB.getName());
            }
        }

        return accounts;
    }

    public Vector<String> getGitLabAccounts() {
        Vector<String> accounts = new Vector<>();
        if (gitlabAccountsSize() == 0)
            return accounts;

        PersistentGitLabAccountManager accountManager = new PersistentGitLabAccountManager();
        for (GitLabAccount account : accountManager.getAccountsState().getValue()) {
            String server = account.getServer().toString();
            String cleanServer = server.replace("https://", "").replace("http://", "");

            if (GitUtils.extractGitInfo(ProjectManager.getInstance().getOpenProjects()[0].getBasePath())
                    .containsKey(cleanServer)) {
                accounts.add(account.getName() + " / " + server + " / " + Platform.GITLAB.getName());
            }
        }

        return accounts;
    }

    private int gitlabAccountsSize() {
        return new PersistentGitLabAccountManager().getAccountsState().getValue().size();
    }

    private int githubAccountsSize() {
        return GHAccountsUtil.getAccounts().size();
    }

    public IssueData getEmptyIssueData() {
        return emptyIssueData;
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

        String[] account = selectedAccount.split(" / ");
        GitInfo gitInfo = GitUtils.extractGitInfo(ProjectManager.getInstance().getOpenProjects()[0].getBasePath()).get(
                account[1].replace("https://", "").replace("http://", "")
        );

        return new GitUser(account[0], account[1], gitInfo.namespace(), gitInfo.url(), Platform.fromName(account[2]));
    }
}
