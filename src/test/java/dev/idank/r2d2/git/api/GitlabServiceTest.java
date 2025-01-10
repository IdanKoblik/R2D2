package dev.idank.r2d2.git.api;

import dev.idank.r2d2.git.data.IssueData;
import dev.idank.r2d2.git.data.UserData;
import dev.idank.r2d2.git.request.GitlabIssueRequest;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.mockwebserver.MockResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public final class GitlabServiceTest extends BaseHttpTest<GitlabIssueRequest, GitlabService> {

    private final UserData userData = new UserData(
            System.getProperty("gitlab.user"),
            System.getProperty("gitlab.token"),
            "https://gitlab.com",
            "IdanKoblik/testing",
            "https://gitlab.com/IdanKoblik/testing.git"
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
    protected void testCreateIssue() throws IOException {
        super.testCreateIssue();
    }

    @Test
    @Override
    protected void testCreateIssueFailure() throws IOException {
        super.testCreateIssueFailure();
    }

    @Test
    @Override
    protected void testFetchIssueData() throws IOException {
        super.testFetchIssueData();

        IssueData result = defaultService.fetchIssueData();

        assertNotNull(result);
        assertTrue(result.labels().isEmpty());
        assertFalse(result.users().isEmpty());
        assertFalse(result.milestones().isEmpty());
    }

    @Test
    @Override
    protected void testInvalidRepo() throws IOException {
        super.testInvalidRepo();
    }

    @Test
    public void testCreateIssueInvalidTitle() throws IOException {
        mockWebServer.enqueue(new MockResponse().setBody("{}").setResponseCode(400));
        Response response = this.defaultService.createIssue(
                new GitlabIssueRequest("|".repeat(300), "test", Set.of(), Set.of(), null)
        );

        assertNotNull(response);
        ResponseBody body = response.body();
        assertNotNull(body);
        assertEquals("{\"message\":{\"title\":[\"is too long (maximum is 255 characters)\"]}}", body.string());
        assertFalse(response.isSuccessful());
        assertEquals(400, response.code());
    }

    @Override
    protected GitlabService constructDefaultService() {
        return new GitlabService(userData);
    }

    @Override
    protected GitlabIssueRequest constructDefaultRequest() {
        return new GitlabIssueRequest("title", "description", null, null, null);
    }

    @Override
    protected GitlabIssueRequest constructNullTitleRequest() {
        return new GitlabIssueRequest(null, "description", null, null, null);
    }

    @Override
    protected GitlabService constructInvalidRepo() {
        return new GitlabService(new UserData(
                System.getProperty("gitlab.user"),
                System.getProperty("gitlab.token"),
                "https://gitlab.com",
                "IdanKoblik/404",
                "https://gitlab.com/IdanKoblik/404.git"
        ));
    }

    @Override
    protected GitlabIssueRequest constructInvalidRequest() {
        return null;
    }

}
