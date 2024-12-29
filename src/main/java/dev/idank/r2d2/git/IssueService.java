package dev.idank.r2d2.git;

import okhttp3.Response;

import java.io.IOException;

public abstract class IssueService {

    protected final UserData data;

    protected IssueService(UserData data) {
        this.data = data;
    }

    public abstract Response createIssue(String title, String description) throws IOException;
}
