package dev.idank.r2d2.git.gitlab;

import com.fasterxml.jackson.databind.JsonNode;
import dev.idank.r2d2.PluginLoader;
import dev.idank.r2d2.git.GitService;
import dev.idank.r2d2.git.data.IssueData;
import dev.idank.r2d2.git.data.UserData;
import dev.idank.r2d2.git.request.GitlabIssueRequest;
import okhttp3.*;

import javax.swing.*;
import java.io.IOException;

import static com.intellij.openapi.updateSettings.impl.PluginDownloader.showErrorDialog;

public class GitlabService extends GitService<GitlabIssueRequest> {

    public GitlabService(UserData data) {
        super(data);
    }

    @Override
    public Response createIssue(GitlabIssueRequest requestData) throws IOException {
        int projectId = fetchGitLabProjectId(1, 1);
        if (projectId == -1)
            return new Response.Builder().code(500).build();

        String url = data.instance() + "/api/v4/projects/" + projectId + "/issues";
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
        int projectId = fetchGitLabProjectId(1, 1);
        if (projectId == -1)
            return PluginLoader.getInstance().getEmptyIssueData();

        String url = data.instance() + "/api/v4/projects/" + projectId + "/labels";
        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + data.token())
                .get()
                .build();

        return new IssueData(
                fetchIssues(request)
        );
    }

    private int fetchGitLabProjectId(int page, int perPage) {
        String baseUrl = data.instance() + "/api/v4/projects";
        String url = baseUrl + "?page=" + page + "&per_page=" + perPage;
        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + data.token())
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.body() == null) {
                showErrorDialog("No response body received from the server.");
                return -1;
            }

            JsonNode jsonArray = objectMapper.readTree(response.body().string());

            if (jsonArray.isEmpty()) {
                showErrorDialog("No GitLab project found for URL: " + data.url());
                return -1;
            }

            for (JsonNode project : jsonArray) {
                if (isProjectUrlMatching(project)) {
                    return project.get("id").asInt();
                }
            }

            return fetchGitLabProjectId(page + 1, perPage);
        } catch (IOException e) {
            e.printStackTrace();
            showErrorDialog("An error occurred while fetching the project ID: " + e.getMessage());
            return -1;
        }
    }

    private boolean isProjectUrlMatching(JsonNode project) {
        String projectUrl = data.url();
        String sshUrl = project.get("ssh_url_to_repo").asText();
        String httpUrl = project.get("http_url_to_repo").asText();
        return sshUrl.equals(projectUrl) || httpUrl.equals(projectUrl);
    }

}
