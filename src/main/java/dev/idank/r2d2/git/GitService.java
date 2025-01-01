package dev.idank.r2d2.git;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.idank.r2d2.git.data.IssueData;
import dev.idank.r2d2.git.data.UserData;
import dev.idank.r2d2.git.request.IssueRequest;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public abstract class GitService<T extends IssueRequest> {

    protected final ObjectMapper objectMapper = new ObjectMapper();
    protected final UserData data;
    protected final OkHttpClient client = new OkHttpClient();

    protected GitService(UserData data) {
        this.data = data;
    }

    public abstract Response createIssue(T request) throws IOException;
    public abstract IssueData fetchIssueData() throws IOException;

    protected Set<String> fetchIssues(Request request) {
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
