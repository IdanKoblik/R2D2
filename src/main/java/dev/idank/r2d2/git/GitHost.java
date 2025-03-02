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

import com.intellij.openapi.project.Project;
import dev.idank.r2d2.git.data.AuthData;
import dev.idank.r2d2.git.data.GitUser;

import java.util.Optional;

public sealed abstract class GitHost extends GitService permits GithubHost, GitlabHost {

    protected final Project project;
    protected final GitUser user;

    protected GitHost(Project project, GitUser user) {
        this.project = project;
        this.user = user;
    }

    public abstract AuthData authData(Project project, GitUser user);

    public Project getProject() {
        return project;
    }

    public GitUser getUser() {
        return user;
    }

    public Optional<AuthData> getAuthData() {
        return Optional.ofNullable(this.authData);
    }

    public static String normalizeURL(String str) {
        return str.replace("http://", "")
                .replace("https://", "");
    }
}
