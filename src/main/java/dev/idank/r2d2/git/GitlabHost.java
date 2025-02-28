package dev.idank.r2d2.git;

import com.intellij.openapi.project.Project;
import com.intellij.util.io.URLUtil;
import dev.idank.r2d2.git.data.AuthData;
import dev.idank.r2d2.git.data.GitUser;
import dev.idank.r2d2.git.data.issue.IssueData;
import dev.idank.r2d2.utils.PluginUtils;
import org.jetbrains.plugins.gitlab.authentication.accounts.GitLabAccount;
import org.jetbrains.plugins.gitlab.authentication.accounts.PersistentGitLabAccountManager;

import java.util.Set;

public class GitlabHost extends GitHost {

    private final PersistentGitLabAccountManager accountManager = new PersistentGitLabAccountManager();
    private final String namespace;

    public GitlabHost(Project project, GitUser user) {
        super(project, user);

        this.authData = authData(project, user);
        this.namespace = URLUtil.encodeURIComponent(this.authData.user().projectInfo().namespace());
    }

    @Override
    public AuthData authData(Project project, GitUser user) {
        String url = normalizeURL(user.instance());
        Set<GitLabAccount> accounts = accountManager.getAccountsState().getValue();

        for (GitLabAccount account : accounts) {
            if (!account.getName().equals(user.username()) || !normalizeURL(account.getServer().toString()).equals(url))
                continue;

            return new AuthData(
                    PluginUtils.INSTANCE.findGitlabUserCredentials(account),
                    user
            );
        }

        return null;
    }

    @Override
    public IssueData fetchIssueData() {
        String url = "%s/api/v4/projects/%s".formatted(
                authData.user().instance(),
                this.namespace
        );

        return new IssueData(
                fetchIssues("%s/labels".formatted(url)),
                fetchUsers("%s/users?exclude_bots=true".formatted(url), "username"),
                fetchMilestones("%s/milestones".formatted(url), "id", "active")
        );
    }

    @Override
    protected String getIssueCreationEndpoint() {
        return "%s/api/v4/projects/%s/issues".formatted(
                authData.user().instance(),
                this.namespace
        );
    }
}
