package dev.idank.r2d2.git;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.idank.r2d2.git.data.IssueData;
import dev.idank.r2d2.git.data.User;
import dev.idank.r2d2.git.data.UserData;
import dev.idank.r2d2.git.request.IssueRequest;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public abstract class GitService<R extends IssueRequest> {

    protected final ObjectMapper objectMapper = new ObjectMapper();
    protected final UserData data;
    protected final OkHttpClient client = new OkHttpClient();

    protected GitService(UserData data) {
        this.data = data;
    }

    public abstract Response createIssue(R request) throws IOException;
    public abstract IssueData fetchIssueData() throws IOException;

    protected Set<User> getUsers(String url, String usernameProperty) {
        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + data.token())
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null)
                return Set.of();

            String responseBody = response.body().string();
            JsonNode jsonArray = objectMapper.readTree(responseBody);

            Set<User> users = new HashSet<>();
            for (JsonNode node : jsonArray) {
                users.add(
                        new User(
                                node.get(usernameProperty).asText(),
                                node.get("id").asInt()
                        )
                );
            }

            return users;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected Set<String> fetchIssues(String url) {
        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + data.token())
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.body() == null)
                return Set.of();

            String responseBody = response.body().string();
            JsonNode jsonArray = objectMapper.readTree(responseBody);

            Set<String> labels = new HashSet<>();
            jsonArray.forEach(node -> labels.add(node.get("name").asText()));
            return labels;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
