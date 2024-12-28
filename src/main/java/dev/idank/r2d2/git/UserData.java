package dev.idank.r2d2.git;

public record UserData(
        String username,
        String token,
        String apiURL,
        Platform platform
) {
    @Override
    public String toString() {
        return "UserData{" +
                "username='" + username + '\'' +
                ", token='" + token + '\'' +
                ", apiURL='" + apiURL + '\'' +
                ", platform=" + platform +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        UserData userData = (UserData) o;
        return username.equals(userData.username) &&
                token.equals(userData.token) &&
                apiURL.equals(userData.apiURL) &&
                platform.equals(userData.platform);
    }
}
