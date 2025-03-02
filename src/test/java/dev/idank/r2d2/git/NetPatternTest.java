/*
 * MIT License
 *
 * Copyright (c) 2025 Idan Koblik
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package dev.idank.r2d2.git;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

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
