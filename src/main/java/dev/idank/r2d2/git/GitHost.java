package dev.idank.r2d2.git;

import com.intellij.openapi.project.Project;
import dev.idank.r2d2.git.data.AuthData;
import dev.idank.r2d2.git.data.GitUser;
import dev.idank.r2d2.git.request.IssueRequest;

import java.util.Optional;

public abstract class GitHost<R extends IssueRequest> extends GitService<R> {

    protected final Project project;
    protected final GitUser user;

    public GitHost(Project project, GitUser user) {
        this.project = project;
        this.user = user;
    }

    public abstract AuthData authData(Project project, GitUser user);

    public Project getProject() {
        return project;
    }

    public GitUser getUser() {
        return user;
    }

    public Optional<AuthData> getAuthData() {
        return Optional.ofNullable(this.authData);
    }

    public static String normalizeURL(String str) {
        return str.replace("http://", "")
                .replace("https://", "");
    }
}
