package dev.idank.r2d2.git.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record GithubIssueRequest(
    @JsonProperty("title") String title,
    @JsonProperty("body") String description,
    @JsonProperty("labels") Set<String> labels,
    @JsonProperty("assignees") Set<String> assignees,
    @JsonProperty("milestone") String milestone
) implements IssueRequest {
}
