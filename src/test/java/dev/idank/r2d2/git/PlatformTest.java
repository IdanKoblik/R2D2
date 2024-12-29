package dev.idank.r2d2.git;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PlatformTest {

    @Test
    public void testPlatformName() {
        Platform github = Platform.GITHUB;
        Platform gitlab = Platform.GITLAB;

        assertEquals("(github)", github.getName());
        assertEquals("(gitlab)", gitlab.getName());
    }

    @Test
    public void testFromName() {
        Platform github = Platform.fromName("(github)");
        assertEquals(Platform.GITHUB, github);

        Platform gitlab = Platform.fromName("(gitlab)");
        assertEquals(Platform.GITLAB, gitlab);
    }
}
