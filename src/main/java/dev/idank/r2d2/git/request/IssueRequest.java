package dev.idank.r2d2.git.request;

import java.util.Set;

public sealed interface IssueRequest permits GithubIssueRequest, GitlabIssueRequest {
    String title();
    String description();
    Set<String> labels();
    String milestone();
}
