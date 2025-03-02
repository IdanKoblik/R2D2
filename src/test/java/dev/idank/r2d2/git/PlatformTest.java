package dev.idank.r2d2.git;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PlatformTest {

    @Test
    void testPlatformName() {
        Platform github = Platform.GITHUB;
        Platform gitlab = Platform.GITLAB;

        assertEquals("github", github.test());
        assertEquals("gitlab", gitlab.test());
    }

    @Test
    void testFromName() {
        Platform github = Platform.fromName("github");
        assertEquals(Platform.GITHUB, github);

        Platform gitlab = Platform.fromName("gitlab");
        assertEquals(Platform.GITLAB, gitlab);

        assertThrows(IllegalArgumentException.class, () -> {
            Platform invalid = Platform.fromName("(invalid)");
        });
    }
}
