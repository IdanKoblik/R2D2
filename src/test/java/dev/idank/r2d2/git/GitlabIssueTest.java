/*
 * MIT License
 *
 * Copyright (c) 2025 Idan Koblik
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
