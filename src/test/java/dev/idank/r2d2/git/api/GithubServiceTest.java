package dev.idank.r2d2.git.api;

import dev.idank.r2d2.git.data.IssueData;
import dev.idank.r2d2.git.data.UserData;
import dev.idank.r2d2.git.request.GithubIssueRequest;
import dev.idank.r2d2.git.request.GitlabIssueRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

public final class GithubServiceTest extends BaseHttpTest<GithubIssueRequest, GithubService> {

    private final UserData userData = new UserData(
            System.getProperty("github.user"),
            System.getProperty("github.token"),
            "https://github.com",
            "IdanKoblik/Testing",
            "https://github.com/IdanKoblik/Testing.git"
    );

    @BeforeEach
    @Override
    protected void setUp() throws IOException {
        super.setUp();
    }

    @AfterEach
    @Override
    protected void tearDown() throws IOException {
        super.tearDown();
    }

    @Test
    @Override
    protected void testFetchIssueData() throws IOException {
        super.testFetchIssueData();

        IssueData result = defaultService.fetchIssueData();

        assertNotNull(result);
        assertFalse(result.labels().isEmpty());
        assertFalse(result.users().isEmpty());
        assertFalse(result.milestones().isEmpty());
    }

    @Test
    @Override
    protected void testCreateIssueFailure() throws IOException {
        super.testCreateIssueFailure();
    }

    @Test
    @Override
    protected void testCreateIssue() throws IOException {
        super.testCreateIssue();
    }

    @Test
    @Override
    protected void testInvalidRepo() throws IOException {
        super.testInvalidRepo();
    }

    @Override
    protected GithubService constructInvalidRepo() {
        return new GithubService(new UserData(
                System.getProperty("github.user"),
                System.getProperty("github.token"),
                "https://github.com",
                "IdanKoblik/invalid",
                "https://gitlab.com/IdanKoblik/invalid.git"
        ));
    }

    @Override
    protected GithubIssueRequest constructInvalidRequest() {
        return null;
    }

    @Override
    protected GithubService constructDefaultService() {
        return new GithubService(userData);
    }

    @Override
    protected GithubIssueRequest constructDefaultRequest() {
        return new GithubIssueRequest("title", "description", null, null, null);
    }

    @Override
    protected GithubIssueRequest constructNullTitleRequest() {
        return new GithubIssueRequest(null, "description", null, null, null);
    }

}
