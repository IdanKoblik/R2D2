package dev.idank.r2d2.git;

public record GitUser(
        String username,
        String instance,
        String namespace,
        String url,
        Platform platform
) {
}
