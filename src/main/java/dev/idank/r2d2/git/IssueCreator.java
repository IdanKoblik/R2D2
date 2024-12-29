package dev.idank.r2d2.git;

import okhttp3.Response;

import java.io.IOException;

public class IssueCreator {

    private final IssueService service;

    public IssueCreator(IssueService service) {
        this.service = service;
    }

    public Response createIssue(String title, String description) {
        try {
            Response issue = service.createIssue(title, description);
            issue.close();
            return issue;
        } catch (IOException e) {
            e.printStackTrace();
            return new Response.Builder().code(500).build();
        }
    }
}
