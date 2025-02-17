package dev.idank.r2d2.git;

import com.intellij.openapi.project.Project;
import dev.idank.r2d2.git.data.AuthData;
import dev.idank.r2d2.git.data.GitUser;

public class GitHostFactory {

    public GitHost<?> createGitHost(Project project, GitUser user) {
        return switch(user.platform()) {
            case GITLAB -> new GitlabHost(project, user);
            case GITHUB -> new GithubHost(project, user);
        };
    }

}
