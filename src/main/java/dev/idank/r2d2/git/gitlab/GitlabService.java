package dev.idank.r2d2.git.gitlab;

import dev.idank.r2d2.git.GitService;
import dev.idank.r2d2.git.data.IssueData;
import dev.idank.r2d2.git.data.UserData;
import dev.idank.r2d2.git.request.GitlabIssueRequest;
import okhttp3.*;

import java.io.IOException;

public class GitlabService extends GitService<GitlabIssueRequest> {

    private final String namespace;

    public GitlabService(UserData data) {
        super(data);
        this.namespace = data.namespace().replace("/", "%2F");
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
        return new IssueData(
                fetchIssues( data.instance() + "/api/v4/projects/" + namespace + "/labels"),
                fetchUsers( data.instance() + "/api/v4/projects/" + namespace + "/users?exclude_bots=true", "username"),
                fetchMilestones( data.instance() + "/api/v4/projects/" + namespace + "/milestones", "id")
        );
    }



}
