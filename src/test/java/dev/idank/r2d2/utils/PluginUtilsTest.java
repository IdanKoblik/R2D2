package dev.idank.r2d2.utils;

import dev.idank.r2d2.GitTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PluginUtilsTest extends GitTest {

    @BeforeEach
    @Override
    protected void setUp() {
        super.setUp();
    }

    @AfterEach
    @Override
    protected void tearDown() {
        super.tearDown();
    }

    @Test
    public void testFindGitlabUserCredentials() {
        String token = PluginUtils.INSTANCE.findGitlabUserCredentials(getDefaultGitlabAccount());
        assertEquals(System.getProperty("gitlab.token"), token);
    }
}
