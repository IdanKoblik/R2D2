/*
MIT License

Copyright (c) 2025 Idan Koblik

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */
package dev.idank.r2d2.git;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.idank.r2d2.git.data.AuthData;
import dev.idank.r2d2.git.data.User;
import dev.idank.r2d2.git.data.issue.IssueData;
import dev.idank.r2d2.git.data.issue.Milestone;
import dev.idank.r2d2.git.request.IssueRequest;
import dev.idank.r2d2.git.response.IssueResponse;
import dev.idank.r2d2.utils.UIUtils;

import javax.swing.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashSet;
import java.util.Set;

public abstract class GitService {

    protected final ObjectMapper objectMapper = new ObjectMapper();
    protected AuthData authData;

    public abstract IssueData fetchIssueData();

    public <T extends IssueResponse> T createIssue(IssueRequest requestData, Class<T> clazz) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(getIssueCreationEndpoint()))
                .header("Authorization", "Bearer " + authData.token())
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(requestData)))
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(response.body(), clazz);
    }

    protected Set<User> fetchUsers(String url, String usernameProperty) {
        JsonNode jsonArray = createGetRequest(url);
        if (jsonArray == null)
            return Set.of();

        Set<User> users = new HashSet<>();
        for (JsonNode node : jsonArray) {
            JsonNode username = node.get(usernameProperty);
            JsonNode id = node.get("id");
            if (username == null || id == null)
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

    protected Set<Milestone> fetchMilestones(String url, String idProperty, String validState) {
        JsonNode jsonArray = createGetRequest(url);
        if (jsonArray == null)
            return Set.of();

        Set<Milestone> milestones = new HashSet<>();
        for (JsonNode node : jsonArray) {
            JsonNode id = node.get(idProperty);
            JsonNode title = node.get("title");
            JsonNode state = node.get("state");
            if (id == null || title == null || state == null)
                continue;

            if (!state.asText().equals(validState))
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
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + authData.token())
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpClient client = HttpClient.newHttpClient();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.body() == null)
                return null;

            String responseBody = response.body();
            return objectMapper.readTree(responseBody);
        } catch (IOException | InterruptedException e) {
            UIUtils.showError("Request failed due to an I/O error: " + e, new JOptionPane());
            return null;
        }
    }

    protected abstract String getIssueCreationEndpoint();
}