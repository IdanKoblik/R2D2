package dev.idank.r2d2.managers;

import dev.idank.r2d2.git.data.GitUser;
import dev.idank.r2d2.git.data.UserData;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class UserManager {

    private final Map<String, GitUser> users = new HashMap<>();
    private final Map<GitUser, UserData> usersData = new HashMap<>();

    private static UserManager instance;

    public static UserManager getInstance() {
        return (instance == null) ? (instance = new UserManager()) : instance;
    }

    public void addUserData(GitUser user, UserData data) {
        this.usersData.put(user, data);
    }

    public Optional<UserData> getUserData(GitUser user) {
        return Optional.ofNullable(this.usersData.get(user));
    }

    public void addUser(GitUser account) {
        this.users.put(account.toString(), account);
    }

    public Optional<GitUser> getUser(String str) {
        return Optional.ofNullable(this.users.get(str));
    }
}
