package dev.idank.r2d2.git;

import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.jetbrains.plugins.github.api.GithubServerPath;
import org.jetbrains.plugins.github.authentication.GHAccountsUtil;
import org.jetbrains.plugins.github.authentication.accounts.GithubAccount;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.EnumMap;
import java.util.Map;

@RunWith(JUnit4.class)
public class GitUserExtractorTest extends BasePlatformTestCase {

    private GitUserExtractor instance;

    @BeforeEach
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        GitUserExtractor.resetInstance();
        instance = GitUserExtractor.getInstance();
    }

    @AfterEach
    @Override
    protected void tearDown() throws Exception {
        GitUserExtractor.resetInstance();
        super.tearDown();
    }

    @Test
    public void testInvalidateCaches() {
        instance.setLastCacheTime(0);

        EnumMap<Platform, UserData> testUsers = new EnumMap<>(Platform.class);
        testUsers.put(
                Platform.GITHUB,
                new UserData("github", "github", "github", Platform.GITHUB)
        );

        instance.setCachedUsers(testUsers);
        instance.invalidateCache();

        assertNull(instance.getCachedUsers());
        assertEquals(-1, instance.getLastCacheTime());
    }

    @Override
    protected String getTestDataPath() {
        return "src/test/testData";
    }

}
