package dev.idank.r2d2.git;

import dev.idank.r2d2.git.data.issue.IssueData;
import dev.idank.r2d2.git.request.IssueRequest;
import okhttp3.Response;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

 class BaseHttpTest<R extends IssueRequest, S extends GitService<R>> {

    protected S defaultService;
    protected R defaultRequest;
    protected MockWebServer mockWebServer;

    public BaseHttpTest(S defaultService, R defaultRequest) throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        this.defaultService = defaultService;
        this.defaultRequest = defaultRequest;
    }

    public void testInvalidRepo(S invalidService, R invalidRequest) throws IOException {
        mockWebServer.enqueue(new MockResponse().setBody("{}").setResponseCode(404));

        Response response = invalidService.createIssue(invalidRequest);
        assertFalse(response.isSuccessful());

        IssueData data = invalidService.fetchIssueData();
        assertTrue(data.labels().isEmpty());
        assertTrue(data.users().isEmpty());
        assertTrue(data.milestones().isEmpty());
    }

    public void testCreateIssue() throws IOException {
        mockWebServer.enqueue(new MockResponse().setBody("{}").setResponseCode(201));
        Response response = defaultService.createIssue(defaultRequest);

        assertNotNull(response);
        assertTrue(response.isSuccessful());
        assertEquals(201, response.code());
    }

    public void testCreateIssueFailure(R nullTitleRequest) throws IOException {
        mockWebServer.enqueue(new MockResponse().setBody("{}").setResponseCode(400));

        Response response = defaultService.createIssue(
                nullTitleRequest
        );

        assertNotNull(response);
        assertFalse(response.isSuccessful());
    }

    public MockWebServer getMockWebServer() {
        return mockWebServer;
    }

    public S getDefaultService() {
        return defaultService;
    }

    public R getDefaultRequest() {
        return defaultRequest;
    }
}