package dev.idank.r2d2.git.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record GitlabIssueRequest(
        @JsonProperty("title") String title,
        @JsonProperty("description") String description,
        @JsonProperty("labels") Set<String> labels,
        @JsonProperty("assignee_ids") Set<Integer> ids,
        @JsonProperty("milestone_id") String milestone
) implements IssueRequest {
}
