package dev.idank.r2d2.git.github;

import com.fasterxml.jackson.databind.JsonNode;
import dev.idank.r2d2.git.GitService;
import dev.idank.r2d2.git.data.IssueData;
import dev.idank.r2d2.git.data.User;
import dev.idank.r2d2.git.data.UserData;
import dev.idank.r2d2.git.request.GithubIssueRequest;
import okhttp3.*;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class GithubService extends GitService<GithubIssueRequest> {

    public GithubService(UserData data) {
        super(data);
    }

    @Override
    public Response createIssue(GithubIssueRequest requestData) throws IOException {
        String url = "%s/repos/%s/issues".formatted(resolveInstance(), data.namespace());
        RequestBody body = RequestBody.create(objectMapper.writeValueAsBytes(requestData), MediaType.get("application/json"));
        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + data.token())
                .post(body)
                .build();

        return client.newCall(request).execute();
    }

    @Override
    public IssueData fetchIssueData() {
        return new IssueData(
                fetchIssues("%s/repos/%s/labels".formatted(resolveInstance(), data.namespace())),
                getUsers("%s/repos/%s/assignees".formatted(resolveInstance(), data.namespace()), "login")
        );
    }

    private String resolveInstance() {
        String instance = data.instance();
        if (instance.startsWith("http://"))
            instance = instance.replaceFirst("http://", "http://api.");
        else if (instance.startsWith("https://"))
            instance = instance.replaceFirst("https://", "https://api.");

        return instance;
    }
}
