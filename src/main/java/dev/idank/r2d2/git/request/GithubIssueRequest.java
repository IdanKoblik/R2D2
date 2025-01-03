package dev.idank.r2d2.git.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;

public record GithubIssueRequest(
    @JsonProperty("title") String title,
    @JsonProperty("body") String description,
    @JsonProperty("labels") Set<String> labels,
    @JsonProperty("assignees") Set<String> assignees
) implements IssueRequest {
}
