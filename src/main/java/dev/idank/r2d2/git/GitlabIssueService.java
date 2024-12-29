package dev.idank.r2d2.git;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import javax.swing.*;
import java.io.IOException;

public class GitlabIssueService extends IssueService {

    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public GitlabIssueService(UserData data) {
        super(data);
    }

    @Override
    public Response createIssue(String title, String description) throws IOException {
        int projectId = getGitLabProjectId();
        if (projectId == -1)
            return new Response.Builder().code(500).build();

        String json = String.format("{\"title\": \"%s\", \"body\": \"%s\"}", title, description);
        String url = data.instance() + "/api/v4/projects/" + projectId + "/issues";

        RequestBody body = RequestBody.create(json, MediaType.get("application/json"));
        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + data.token())
                .post(body)
                .build();

        return client.newCall(request).execute();
    }

    private int getGitLabProjectId() {
        String url = data.instance() + "/api/v4/projects/";
        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + data.token())
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.body() == null)
                return -1;

            String responseBody = response.body().string();
            JsonNode jsonArray = objectMapper.readTree(responseBody);

            System.out.println(responseBody);
            for (JsonNode project : jsonArray) {
                if (project.get("ssh_url_to_repo").asText().equals(data.url()) || project.get("http_url_to_repo").asText().equals(data.url())) {
                    return project.get("id").asInt();
                }
            }

            JOptionPane.showMessageDialog(null, "No GitLab project found for url: " + data.url(),
                    "Project Not Found", JOptionPane.ERROR_MESSAGE);

            return -1;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

}
