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

package dev.idank.r2d2.git.data;

import dev.idank.r2d2.git.Platform;

/**
 * Git user
 * @param username - the git username
 * @param instance - the instance of the git hosting server (<a href="https://github.com">...</a>, <a href="https://gitlab.com">...</a>, e.g)
 * @param projectInfo - the projectInfo of a git repo
 * @param platform - the platform of a git hosting (github, gitlab, e.g)
 */
public record GitUser(
        String username,
        String instance,
        GitProjectInfo projectInfo,
        Platform platform
) {

    @Override
    public String toString() {
        return "%s / %s / %s [%s]".formatted(username, instance, platform, projectInfo.namespace());
    }
}
