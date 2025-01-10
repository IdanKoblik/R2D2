package dev.idank.r2d2.git.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.idank.r2d2.git.data.IssueData;
import dev.idank.r2d2.git.data.Milestone;
import dev.idank.r2d2.git.data.User;
import dev.idank.r2d2.git.data.UserData;
import dev.idank.r2d2.git.request.IssueRequest;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public sealed abstract class GitService<R extends IssueRequest> permits GithubService, GitlabService {

    protected final ObjectMapper objectMapper = new ObjectMapper();
    protected final OkHttpClient client = new OkHttpClient();

    protected final UserData data;

    protected GitService(UserData data) {
        this.data = data;
    }

    public abstract Response createIssue(R request) throws IOException;
    public abstract IssueData fetchIssueData() throws IOException;

    protected Set<User> fetchUsers(String url, String usernameProperty) {
        JsonNode jsonArray = createGetRequest(url);
        if (jsonArray == null)
            return Set.of();

        Set<User> users = new HashSet<>();
        for (JsonNode node : jsonArray) {
            JsonNode username = node.get(usernameProperty);
            if (username == null)
                continue;

            JsonNode id = node.get("id");
            if (id == null)
                continue;

            users.add(
                    new User(
                            username.asText(),
                            id.asInt()
                    )
            );
        }

        return users;
    }

    protected Set<String> fetchIssues(String url) {
        JsonNode jsonArray = createGetRequest(url);
        if (jsonArray == null)
            return Set.of();

        Set<String> labels = new HashSet<>();
        for (JsonNode node : jsonArray) {
            if (node.get("name") == null)
                continue;

            labels.add(node.get("name").asText());
        }

        return labels;
    }

    protected Set<Milestone> fetchMilestones(String url, String idProperty) {
        JsonNode jsonArray = createGetRequest(url);
        if (jsonArray == null)
            return Set.of();

        Set<Milestone> milestones = new HashSet<>();
        for (JsonNode node : jsonArray) {
            JsonNode id = node.get(idProperty);
            if (id == null)
                continue;

            JsonNode title = node.get("title");
            if (title == null)
                continue;

            milestones.add(
                    new Milestone(
                            id.asText(),
                            title.asText()
                    )
            );
        }

        return milestones;
    }

    private JsonNode createGetRequest(String url) {
        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + data.token())
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.body() == null)
                return null;

            String responseBody = response.body().string();
            return objectMapper.readTree(responseBody);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
