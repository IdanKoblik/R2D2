package dev.idank.r2d2.utils;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import git4idea.repo.GitRemote;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryImpl;
import org.jetbrains.annotations.TestOnly;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

public class GitUtils {

    private static final String HEAD_FILE_NAME = "HEAD";
    private static final String GIT_DIRECTORY_NAME = ".git";
    private static final String MASTER_REF = "ref: refs/heads/master";
    private static final String TEST_REMOTE_NAME = "test";
    private static final List<String> TEST_GITLAB_URLS = List.of("https://gitlab.com/tester/repo.git");
    private static final List<String> TEST_GITHUB_URLS = List.of("https://github.com/tester/repo.git");

    @TestOnly
    public static Repo createDummyRepo(Project project) {
        Path gitPath = createGitDirectory(project);
        createHeadFile(gitPath);

        GitRepository repo = createGitRepository(project, gitPath);
        addRemotes(repo);

        return new Repo(repo, gitPath.toFile());
    }

    private static Path createGitDirectory(Project project) {
        Path gitPath = Paths.get(UUID.randomUUID().toString(), project.getBasePath(), GIT_DIRECTORY_NAME);
        try {
            Files.createDirectories(gitPath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create .git directory", e);
        }
        return gitPath;
    }

    private static void createHeadFile(Path gitPath) {
        Path headFilePath = gitPath.resolve(HEAD_FILE_NAME);
        try {
            Files.createDirectories(headFilePath.getParent());
            Files.writeString(headFilePath, MASTER_REF);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create HEAD file", e);
        }
    }

    private static GitRepository createGitRepository(Project project, Path gitPath) {
        return GitRepositoryImpl.createInstance(
                project.getBaseDir(),
                LocalFileSystem.getInstance().findFileByIoFile(gitPath.toFile()),
                project,
                () -> {}
        );
    }

    private static void addRemotes(GitRepository repo) {
        repo.getRemotes().add(new GitRemote(TEST_REMOTE_NAME, TEST_GITLAB_URLS, TEST_GITLAB_URLS, TEST_GITLAB_URLS, TEST_GITLAB_URLS));
        repo.getRemotes().add(new GitRemote(TEST_REMOTE_NAME, TEST_GITHUB_URLS, TEST_GITHUB_URLS, TEST_GITHUB_URLS, TEST_GITHUB_URLS));
    }

    public record Repo(
            GitRepository repo,
            File file
    ) {}
}