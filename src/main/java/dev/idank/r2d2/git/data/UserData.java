package dev.idank.r2d2.git.data;

public record UserData(
        String username,
        String token,
        String instance,
        String namespace,
        String url
) {

}