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

import ai.grazie.annotation.TestOnly;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.idank.r2d2.git.data.AuthData;
import dev.idank.r2d2.git.data.User;
import dev.idank.r2d2.git.data.issue.IssueData;
import dev.idank.r2d2.git.data.issue.Milestone;
import dev.idank.r2d2.git.request.IssueRequest;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public abstract class GitService {

    private final Object lock = new Object();

    protected final ObjectMapper objectMapper = new ObjectMapper();
    protected OkHttpClient client;

    protected AuthData authData;

    protected GitService(OkHttpClient client) {
        this.client = client;
    }

    public abstract Response createIssue(IssueRequest request) throws IOException;
    public abstract IssueData fetchIssueData() throws IOException;

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
        synchronized (lock) {
            Request request = new Request.Builder()
                    .url(url)
                    .header("Authorization", "Bearer " + authData.token())
                    .get()
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.body() == null)
                    return null;

                String responseBody = response.body().string();
                return objectMapper.readTree(responseBody);
            } catch (IOException e) {
                throw new RuntimeException("Request failed due to an I/O error", e);
            } finally {
                client.dispatcher().executorService().shutdown();
            }
        }
    }

    @TestOnly
    public void setClient(OkHttpClient client) {
        this.client = client;
    }
}