package dev.idank.r2d2.git.api;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NetPatternTest {

    @Test
    void testGetNetPattern_SSH() {
        final String SSH_URL = "git@example.com:me/repo.git";
        NetPattern result = NetPattern.getNetPattern(SSH_URL);
        assertEquals(NetPattern.SSH, result, "The URL should match the SSH pattern.");
    }

    @Test
    void testGetNetPattern_HTTPS() {
        final String HTTPS_URL = "https://example.com/me/repo.git";
        NetPattern result = NetPattern.getNetPattern(HTTPS_URL);
        assertEquals(NetPattern.HTTPS, result, "The URL should match the HTTPS pattern.");
    }

    @Test
    void testGetNetPattern_HTTP() {
        final String HTTP_URL = "http://example.com/me/repo.git";
        NetPattern result = NetPattern.getNetPattern(HTTP_URL);
        assertEquals(NetPattern.HTTP, result, "The URL should match the HTTP pattern.");
    }

    @Test
    void testGetNetPattern_InvalidUrl() {
        String invalidUrl = "ftp://example.com";
        NetPattern result = NetPattern.getNetPattern(invalidUrl);
        assertNull(result, "The URL should not match any of the patterns.");
    }

    @Test
    void testGetNetPattern_EmptyUrl() {
        String emptyUrl = "";
        NetPattern result = NetPattern.getNetPattern(emptyUrl);
        assertNull(result, "An empty URL should not match any of the patterns.");
    }
}
