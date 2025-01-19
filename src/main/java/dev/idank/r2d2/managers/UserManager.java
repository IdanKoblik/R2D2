/*
MIT License

Copyright (c) 2025 Idan Koblik

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */
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

    public void clear() {
        this.users.clear();
        this.usersData.clear();
    }
}
