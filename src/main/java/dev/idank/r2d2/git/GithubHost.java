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

import com.intellij.openapi.project.Project;
import dev.idank.r2d2.git.data.AuthData;
import dev.idank.r2d2.git.data.GitUser;
import dev.idank.r2d2.git.data.issue.IssueData;
import org.jetbrains.plugins.github.authentication.accounts.GHAccountManager;
import org.jetbrains.plugins.github.authentication.accounts.GithubAccount;
import org.jetbrains.plugins.github.util.GHCompatibilityUtil;

import java.util.Optional;

public final class GithubHost extends GitHost {

    public GithubHost(Project project, GitUser user) {
        super(project, user);
        this.authData = authData(project, user);
    }

    @Override
    public AuthData authData(Project project, GitUser user) {
        String url = normalizeURL(user.instance());

        Optional<GithubAccount> ghAccountOpt = new GHAccountManager().getAccountsState().getValue()
                .stream()
                .filter(account ->
                        account.getName().equals(user.username())
                                && normalizeURL(account.getServer().toString()).equals(url)
                ).findFirst();

        if (ghAccountOpt.isEmpty())
            return null;

        GithubAccount ghAccount = ghAccountOpt.get();
        String token = GHCompatibilityUtil.getOrRequestToken(ghAccount, project);
        return new AuthData(
                token,
                user
        );
    }

    @Override
    public IssueData fetchIssueData() {
        String namespace = authData.user().projectInfo().namespace();
        return new IssueData(
                fetchIssues("%s/repos/%s/labels".formatted(resolveInstance(), namespace)),
                fetchUsers("%s/repos/%s/assignees".formatted(resolveInstance(), namespace), "login"),
                fetchMilestones("%s/repos/%s/milestones".formatted(resolveInstance(), namespace), "number", "open")
        );
    }

    private String resolveInstance() {
        String instance = authData.user().instance();
        if (instance.startsWith("http://"))
            instance = instance.replaceFirst("http://", "http://api.");
        else if (instance.startsWith("https://"))
            instance = instance.replaceFirst("https://", "https://api.");

        return instance;
    }

    @Override
    protected String getIssueCreationEndpoint() {
        return "%s/repos/%s/issues".formatted(resolveInstance(), authData.user().projectInfo().namespace());
    }
}
