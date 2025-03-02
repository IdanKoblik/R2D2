package dev.idank.r2d2.git.response;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NONE
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = GithubIssueResponse.class),
        @JsonSubTypes.Type(value = GitlabIssueResponse.class)
})
public sealed interface IssueResponse permits GitlabIssueResponse, GithubIssueResponse {
    String url();
    String title();
    String body();
}
