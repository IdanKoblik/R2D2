package dev.idank.r2d2.git.api;

import dev.idank.r2d2.git.data.IssueData;
import dev.idank.r2d2.git.data.UserData;
import dev.idank.r2d2.git.request.IssueRequest;
import okhttp3.Response;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

/* package-private */ sealed abstract class BaseHttpTest<R extends IssueRequest, S extends GitService<R>> permits GithubServiceTest, GitlabServiceTest {

    protected S defaultService;
    protected R defaultRequest;
    protected MockWebServer mockWebServer;

    @BeforeEach
    protected void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        this.defaultService = constructDefaultService();
        this.defaultRequest = constructDefaultRequest();
    }

    @AfterEach
    protected void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    protected void testInvalidRepo() throws IOException {
        mockWebServer.enqueue(new MockResponse().setBody("{}").setResponseCode(404));
        S invalidService = constructInvalidRepo();

        Response response = invalidService.createIssue(constructInvalidRequest());
        assertFalse(response.isSuccessful());

        IssueData data = invalidService.fetchIssueData();
        assertTrue(data.labels().isEmpty());
        assertTrue(data.users().isEmpty());
        assertTrue(data.milestones().isEmpty());
    }

    @Test
    protected void testCreateIssue() throws IOException {
        mockWebServer.enqueue(new MockResponse().setBody("{}").setResponseCode(201));
        Response response = defaultService.createIssue(defaultRequest);

        assertNotNull(response);
        assertTrue(response.isSuccessful());
        assertEquals(201, response.code());
    }

    @Test
    protected void testCreateIssueFailure() throws IOException {
        mockWebServer.enqueue(new MockResponse().setBody("{}").setResponseCode(400));

        Response response = defaultService.createIssue(
                constructNullTitleRequest()
        );

        assertNotNull(response);
        assertFalse(response.isSuccessful());
    }

    @Test
    protected void testFetchIssueData() throws IOException {
        mockWebServer.enqueue(new MockResponse().setBody("[]").setResponseCode(200));
        mockWebServer.enqueue(new MockResponse().setBody("[]").setResponseCode(200));
        mockWebServer.enqueue(new MockResponse().setBody("[]").setResponseCode(200));
    }

    protected abstract S constructDefaultService();
    protected abstract R constructDefaultRequest();
    protected abstract R constructNullTitleRequest();
    protected abstract S constructInvalidRepo();
    protected abstract R constructInvalidRequest();
}
