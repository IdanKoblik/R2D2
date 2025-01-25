package dev.idank.r2d2.utils;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import git4idea.repo.GitRemote;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryImpl;
import org.jetbrains.annotations.TestOnly;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class GitUtils {

    @TestOnly
    public static Repo createDummyRepo(Project project) {
        File git = new File(UUID.randomUUID() + "/" + project.getBasePath() + "/.git");
        git.mkdirs();

        String headFilePath = git.getPath() + "/HEAD";
        File headFile = new File(headFilePath);

        headFile.getParentFile().mkdirs();

        try (FileWriter writer = new FileWriter(headFile)) {
            writer.write("ref: refs/heads/master");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        GitRepository repo = GitRepositoryImpl.createInstance(
                project.getBaseDir(),
                LocalFileSystem.getInstance().findFileByIoFile(git),
                project
                , () -> {}
        );

        repo.getRemotes()
                .add(new GitRemote(
                        "test",
                        List.of("https://gitlab.com/tester/repo.git"),
                        List.of("https://gitlab.com/tester/repo.git"),
                        List.of("https://gitlab.com/tester/repo.git"),
                        List.of("https://gitlab.com/tester/repo.git")
                ));

        repo.getRemotes()
                .add(new GitRemote(
                        "test",
                        List.of("https://github.com/tester/repo.git"),
                        List.of("https://github.com/tester/repo.git"),
                        List.of("https://github.com/tester/repo.git"),
                        List.of("https://github.com/tester/repo.git")
                ));

        return new Repo(
                repo,
                git
        );
    }

    public record Repo(
            GitRepository repo,
            File file
    ) {}
}
