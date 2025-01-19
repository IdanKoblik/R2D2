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
package dev.idank.r2d2.git.api;

import dev.idank.r2d2.git.data.IssueData;
import dev.idank.r2d2.git.data.UserData;
import dev.idank.r2d2.git.request.GithubIssueRequest;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;

public final class GithubService extends GitService<GithubIssueRequest> {

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
                fetchUsers("%s/repos/%s/assignees".formatted(resolveInstance(), data.namespace()), "login"),
                fetchMilestones("%s/repos/%s/milestones".formatted(resolveInstance(), data.namespace()), "number", "open")
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
