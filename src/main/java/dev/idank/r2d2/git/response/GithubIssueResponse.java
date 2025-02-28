package dev.idank.r2d2.git.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import dev.idank.r2d2.git.deserializer.GithubIssueResponseDeserializer;

@JsonDeserialize(using = GithubIssueResponseDeserializer.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record GithubIssueResponse(
        String url,
        String title,
        String body
) implements IssueResponse {
}
