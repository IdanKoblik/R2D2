package dev.idank.r2d2.git;

import com.intellij.openapi.project.Project;
import dev.idank.r2d2.git.data.AuthData;
import dev.idank.r2d2.git.data.GitUser;
import dev.idank.r2d2.git.data.issue.IssueData;
import org.jetbrains.plugins.github.authentication.accounts.GHAccountManager;
import org.jetbrains.plugins.github.authentication.accounts.GithubAccount;
import org.jetbrains.plugins.github.util.GHCompatibilityUtil;

import java.util.Optional;

public class GithubHost extends GitHost {

    public GithubHost(Project project, GitUser user) {
        super(project, user);
        this.authData = authData(project, user);
    }

    @Override
    public AuthData authData(Project project, GitUser user) {
        String url = normalizeURL(user.instance());

        Optional<GithubAccount> ghAccountOpt = new GHAccountManager().getAccountsState().getValue()
                .stream()
                .filter(account ->
                        account.getName().equals(user.username())
                                && normalizeURL(account.getServer().toString()).equals(url)
                ).findFirst();

        if (ghAccountOpt.isEmpty())
            return null;

        GithubAccount ghAccount = ghAccountOpt.get();
        String token = GHCompatibilityUtil.getOrRequestToken(ghAccount, project);
        return new AuthData(
                token,
                user
        );
    }

    @Override
    public IssueData fetchIssueData() {
        String namespace = authData.user().projectInfo().namespace();
        return new IssueData(
                fetchIssues("%s/repos/%s/labels".formatted(resolveInstance(), namespace)),
                fetchUsers("%s/repos/%s/assignees".formatted(resolveInstance(), namespace), "login"),
                fetchMilestones("%s/repos/%s/milestones".formatted(resolveInstance(), namespace), "number", "open")
        );
    }

    private String resolveInstance() {
        String instance = authData.user().instance();
        if (instance.startsWith("http://"))
            instance = instance.replaceFirst("http://", "http://api.");
        else if (instance.startsWith("https://"))
            instance = instance.replaceFirst("https://", "https://api.");

        return instance;
    }

    @Override
    protected String getIssueCreationEndpoint() {
        return "%s/repos/%s/issues".formatted(resolveInstance(), authData.user().projectInfo().namespace());
    }
}
