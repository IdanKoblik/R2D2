package dev.idank.r2d2.git;

import com.intellij.openapi.project.Project;
import com.intellij.util.AuthData;
import kotlin.jvm.functions.Function2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;
import org.jetbrains.plugins.github.authentication.GHAccountAuthData;
import org.jetbrains.plugins.github.authentication.GHAccountsUtil;
import org.jetbrains.plugins.github.authentication.accounts.GithubAccount;
import org.jetbrains.plugins.github.util.GHCompatibilityUtil;
import org.jetbrains.plugins.gitlab.api.GitLabServerPath;
import org.jetbrains.plugins.gitlab.authentication.GitLabLoginUtil;
import org.jetbrains.plugins.gitlab.authentication.LoginResult;
import org.jetbrains.plugins.gitlab.authentication.accounts.GitLabAccount;
import org.jetbrains.plugins.gitlab.authentication.accounts.PersistentGitLabAccountManager;
import org.jetbrains.plugins.gitlab.git.http.GitLabHttpAuthDataProvider;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiPredicate;

public class GitUserExtractor {

    private static GitUserExtractor instance;
    private EnumMap<Platform, UserData> cachedUsers = null;
    private long lastCacheTime = -1;
    private static final long CACHE_EXPIRY_TIME_MS = 5 * 60 * 1000; // 5 minutes

    private GitUserExtractor() {}

    public static synchronized GitUserExtractor getInstance() {
        if (instance == null) {
            instance = new GitUserExtractor();
        }

        return instance;
    }

    @TestOnly
    public static void resetInstance() {
        instance = null;
    }

    public synchronized Map<Platform, UserData> extractUsers(@NotNull Project project) {
        if (cachedUsers == null || System.currentTimeMillis() - lastCacheTime > CACHE_EXPIRY_TIME_MS) {
            this.cachedUsers = new EnumMap<>(Platform.class);
            extractGithubUserData(project, cachedUsers);
            extractGitLabUserData(project, cachedUsers);
            lastCacheTime = System.currentTimeMillis();
        }

        return Collections.unmodifiableMap(cachedUsers);
    }

    private void extractGithubUserData(@NotNull Project project, EnumMap<Platform, UserData> users) {
        GithubAccount account = GHAccountsUtil.getDefaultAccount(project);

        if (account == null) {
            GHAccountAuthData newAccount = GHAccountsUtil.requestNewAccount(project);
            if (newAccount != null) {
                users.put(Platform.GITHUB, new UserData(
                        newAccount.getAccount().getName(),
                        newAccount.getToken(),
                        newAccount.getServer().toApiUrl(),
                        Platform.GITHUB
                ));
            }
            return;
        }

        try {
            String token = GHCompatibilityUtil.getOrRequestToken(account, project);
            users.put(Platform.GITHUB, new UserData(
                    account.getName(),
                    token,
                    account.getServer().toApiUrl(),
                    Platform.GITHUB
            ));
        } catch (Exception e) {
            System.out.println("Failed to fetch GitHub token: " + e.getMessage());
        }
    }

    private void extractGitLabUserData(@NotNull Project project, EnumMap<Platform, UserData> users) {
        PersistentGitLabAccountManager accountManager = new PersistentGitLabAccountManager();
        Set<GitLabAccount> gitlabAccounts = accountManager.getAccountsState().getValue();

        Function2<GitLabServerPath, String, Boolean> uniqueAccountPredicate = (path, name) -> true;
        if (gitlabAccounts.isEmpty()) {
            LoginResult loginResult = GitLabLoginUtil.INSTANCE.logInViaToken$intellij_vcs_gitlab(
                    project,
                    new JPanel(new GridBagLayout()),
                    new GitLabServerPath("https://gitlab.com"),
                    uniqueAccountPredicate
                    );
            if (!(loginResult instanceof LoginResult.Success success)) {
                System.out.println("No GitLab accounts found.");
                return;
            }

            GitLabAccount account = success.getAccount();
            users.put(Platform.GITLAB, new UserData(
                    account.getName(),
                    success.getToken(),
                    account.getServer().getRestApiUri().toString(),
                    Platform.GITLAB
            ));
            return;
        }

        GitLabAccount first = gitlabAccounts.stream().findFirst().orElseThrow();
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            try {
                GitLabHttpAuthDataProvider gitLabHttpAuthDataProvider = new GitLabHttpAuthDataProvider();
                AuthData authData = gitLabHttpAuthDataProvider.getAuthData(project, first.getServer().getUri());

                if (authData != null && authData.getLogin().equals(first.getName())) {
                    users.put(Platform.GITLAB, new UserData(
                            authData.getLogin(),
                            authData.getPassword(),
                            first.getServer().getRestApiUri().toString(),
                            Platform.GITLAB
                    ));
                } else {
                    System.out.println("GitLab auth data mismatch or not found.");
                }
            } catch (Exception e) {
                System.out.println("Failed to fetch GitLab Auth Data: " + e.getMessage());
            } finally {
                executorService.shutdown();
            }
        });
    }

    public synchronized void invalidateCache() {
        this.cachedUsers = null;
        this.lastCacheTime = -1;
    }

    public synchronized long getLastCacheTime() {
        return lastCacheTime;
    }

    @TestOnly
    public synchronized Map<Platform, UserData> getCachedUsers() {
        return cachedUsers != null ? Collections.unmodifiableMap(cachedUsers) : null;
    }

    @TestOnly
    public synchronized void setLastCacheTime(long lastCacheTime) {
        this.lastCacheTime = lastCacheTime;
    }

    @TestOnly
    public synchronized void setCachedUsers(EnumMap<Platform, UserData> cachedUsers) {
        this.cachedUsers = cachedUsers;
    }
}