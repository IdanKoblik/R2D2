package dev.idank.r2d2.git;

public record UserData(
        String username,
        String token,
        String instance,
        String namespace,
        String url
) {

}