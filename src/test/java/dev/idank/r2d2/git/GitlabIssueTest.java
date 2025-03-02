package dev.idank.r2d2.git;

import dev.idank.r2d2.git.data.AuthData;
import dev.idank.r2d2.git.data.GitProjectInfo;
import dev.idank.r2d2.git.data.GitUser;
import dev.idank.r2d2.git.request.GitlabIssueRequest;
import dev.idank.r2d2.git.response.GitlabIssueResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GitlabIssueTest extends GitHostTest<GitlabIssueRequest, GitlabIssueResponse> {

    protected GitlabIssueTest() {
        super(GitlabIssueResponse.class, Platform.GITLAB);
    }

    @BeforeEach
    @Override
    protected void setUp() {
        setPlatform(Platform.GITLAB);
        super.setUp();

        GitUser gitlabGitUser = new GitUser(
                getDefaultGitlabAccount().getName(),
                getDefaultGitlabAccount().getServer().toString(),
                new GitProjectInfo(
                        getDefaultGitlabAccount().getName() + "/" + Platform.GITLAB.test(),
                        "git@" + getDefaultGitlabAccount().getServer().toString()
                                .replace("http://", "")
                                .replace("https://", "") + ":" + getDefaultGitlabAccount().getName() + "/" + Platform.GITLAB.test() + ".git"
                ),
                Platform.GITLAB
        );

        host = new GitHostFactory().createGitHost(project, gitlabGitUser);
        assertEquals(new AuthData(getDefaultGitlabAccount().getId(), gitlabGitUser), host.getAuthData().orElseThrow());
    }

    @AfterEach
    @Override
    protected void tearDown() {
        super.tearDown();
    }

    @Test
    public void testCreateIssueWithAllFields() throws IOException, InterruptedException {
        super.testCreateIssueWithAllFields();
    }

    @Test
    public void testCreateIssueWithErrorResponse() {
        super.testCreateIssueWithErrorResponse();
    }

    @Test
    @Override
    public void testGitHostData() {
        super.testGitHostData();
    }

    @Override
    public GitlabIssueRequest getValidRequest() {
        return new GitlabIssueRequest(
                "Test Issue",
                "Test Description",
                Set.of("bug"),
                Set.of(),
                null
        );
    }

    @Override
    public GitlabIssueRequest getInvalidRequest() {
        return new GitlabIssueRequest(
                null,
                "null",
                Set.of(),
                Set.of(),
                null
        );
    }
}
