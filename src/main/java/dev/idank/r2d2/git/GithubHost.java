package dev.idank.r2d2.git;

import com.intellij.openapi.project.Project;
import dev.idank.r2d2.git.data.AuthData;
import dev.idank.r2d2.git.data.GitUser;
import dev.idank.r2d2.git.data.issue.IssueData;
import dev.idank.r2d2.git.request.IssueRequest;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.jetbrains.plugins.github.authentication.accounts.GHAccountManager;
import org.jetbrains.plugins.github.authentication.accounts.GithubAccount;
import org.jetbrains.plugins.github.util.GHCompatibilityUtil;

import java.io.IOException;
import java.util.Optional;

public class GithubHost extends GitHost {

    private final Object lock = new Object();

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
    public Response createIssue(IssueRequest requestData) throws IOException {
        synchronized (lock) {
            String url = "%s/repos/%s/issues".formatted(resolveInstance(), authData.user().projectInfo().namespace());
            RequestBody body = RequestBody.create(objectMapper.writeValueAsBytes(requestData), MediaType.get("application/json"));
            Request request = new Request.Builder()
                    .url(url)
                    .header("Authorization", "Bearer " + authData.token())
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                return response;
            } finally {
                client.dispatcher().executorService().shutdown();
            }
        }
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

}
