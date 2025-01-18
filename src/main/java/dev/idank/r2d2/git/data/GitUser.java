package dev.idank.r2d2.git.data;

import dev.idank.r2d2.git.Platform;

public record GitUser(
        String username,
        String instance,
        String namespace,
        String url,
        Platform platform
) {

    @Override
    public String toString() {
        return "%s / %s / %s [%s]".formatted(username, instance, platform, namespace);
    }
}
