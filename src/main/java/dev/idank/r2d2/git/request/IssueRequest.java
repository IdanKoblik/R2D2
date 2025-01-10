package dev.idank.r2d2.git.request;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Set;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({
        @JsonSubTypes.Type(GithubIssueRequest.class),
        @JsonSubTypes.Type(GitlabIssueRequest.class)
})
public sealed interface IssueRequest permits GithubIssueRequest, GitlabIssueRequest {
    String title();
    String description();
    Set<String> labels();
    String milestone();
}
