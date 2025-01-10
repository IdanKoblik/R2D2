package dev.idank.r2d2.git.api;

import dev.idank.r2d2.git.data.IssueData;
import dev.idank.r2d2.git.data.UserData;
import dev.idank.r2d2.git.request.GitlabIssueRequest;
import okhttp3.*;

import java.io.IOException;

import static com.intellij.util.io.URLUtil.encodeURIComponent;

public final class GitlabService extends GitService<GitlabIssueRequest> {

    private final String namespace;

    public GitlabService(UserData data) {
        super(data);

        this.namespace = encodeURIComponent(data.namespace());
    }

    @Override
    public Response createIssue(GitlabIssueRequest requestData) throws IOException {
        String url = data.instance() + "/api/v4/projects/" + namespace + "/issues";
        RequestBody body = RequestBody.create(objectMapper.writeValueAsBytes(requestData), MediaType.get("application/json"));
        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + data.token())
                .post(body)
                .build();

        return client.newCall(request).execute();
    }

    @Override
    public IssueData fetchIssueData()  {
        String baseURL = data.instance() + "/api/v4/projects/" + namespace;
        return new IssueData(
                fetchIssues( baseURL + "/labels"),
                fetchUsers( baseURL + "/users?exclude_bots=true", "username"),
                fetchMilestones(baseURL + "/milestones", "id")
        );
    }

}
