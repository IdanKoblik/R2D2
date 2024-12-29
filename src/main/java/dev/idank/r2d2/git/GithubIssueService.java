package dev.idank.r2d2.git;

import okhttp3.*;

import java.io.IOException;

public class GithubIssueService extends IssueService {

    public GithubIssueService(UserData data) {
        super(data);
    }

    @Override
    public Response createIssue(String title, String description) throws IOException {
        OkHttpClient client = new OkHttpClient();
        String json = String.format("{\"title\": \"%s\", \"body\": \"%s\"}", title, description);

        String instance = data.instance();
        if (instance.startsWith("http://"))
            instance = instance.replaceFirst("http://", "http://api.");
        else if (instance.startsWith("https://"))
            instance = instance.replaceFirst("https://", "https://api.");

        String url = "%s/repos/%s/issues".formatted(instance, data.namespace());
        RequestBody body = RequestBody.create(json, MediaType.get("application/json"));
        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + data.token())
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null)
                throw new IOException("Unexpected code " + response);

            return response;
        }
    }
}
