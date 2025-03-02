package dev.idank.r2d2.git;

import dev.idank.r2d2.GitTest;
import dev.idank.r2d2.git.request.IssueRequest;
import dev.idank.r2d2.git.response.IssueResponse;
import org.junit.jupiter.api.Assertions;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public abstract class GitHostTest<T extends IssueRequest, E extends IssueResponse> extends GitTest {

    protected final Class<E> responseType;
    protected final Platform platform;

    protected GitHost host;

    protected GitHostTest(Class<E> responseType, Platform platform) {
        this.responseType = responseType;
        this.platform = platform;
    }

    @Override
    protected void setUp() {
        setPlatform(platform);
        super.setUp();
    }

    @Override
    protected void tearDown() {
        super.tearDown();
    }

    public void testCreateIssueWithAllFields() throws IOException, InterruptedException {
        T validRequest = getValidRequest();
        E response = host.createIssue(validRequest, responseType);
        Assertions.assertNotNull(response);
        assertEquals(validRequest.title(), response.title());
        assertEquals(validRequest.description(), response.body());
    }

    public void testCreateIssueWithErrorResponse() {
        assertThrows(NullPointerException.class, () -> host.createIssue(getInvalidRequest(), responseType));
    }

    public void testGitHostData() {
        assertEquals(project, host.getProject());
        assertNotNull(host.getUser());
    }

    public abstract T getInvalidRequest();
    public abstract T getValidRequest();
}
