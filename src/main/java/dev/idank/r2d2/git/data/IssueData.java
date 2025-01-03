package dev.idank.r2d2.git.data;

import java.util.Set;

public record IssueData(
        Set<String> labels,
        Set<User> users
) {
}
