package dev.idank.r2d2.utils;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import dev.idank.r2d2.dialogs.CreateIssueDialog;
import dev.idank.r2d2.git.GitInfo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GitUtils {

    public static Map<String, GitInfo> extractGitInfo(String repoPath, CreateIssueDialog dialog) {
        Map<String, GitInfo> namespaces = new HashMap<>();
        File configFile = new File(repoPath + "/.git/config");

        if (!configFile.exists()) {
            dialog.showError("You are not inside a git repository");
            return namespaces;
        }

        List<String> configContent = null;
        try {
            configContent = Files.readAllLines(configFile.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (String line : configContent) {
            line = line.trim();

            if (line.startsWith("url =")) {
                String url = line.substring("url = ".length()).trim();

                String sshRegex = "git@([a-zA-Z0-9.-]+):(.*?)(\\.git)?$";
                String httpsRegex = "https://([a-zA-Z0-9.-]+)/(.+?)(\\.git)?$";

                if (url.matches(sshRegex)) {
                    String host = url.replaceAll(sshRegex, "$1");
                    String namespace = url.replaceAll(sshRegex, "$2");
                    namespaces.put(host, new GitInfo(namespace, url));
                } else if (url.matches(httpsRegex)) {
                    String host = url.replaceAll(httpsRegex, "$1");
                    String namespace = url.replaceAll(httpsRegex, "$2");
                    namespaces.put(host, new GitInfo(namespace, url));
                }
            }
        }

        return namespaces;
    }
}