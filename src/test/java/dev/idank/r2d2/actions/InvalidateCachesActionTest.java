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

package dev.idank.r2d2.actions;

import com.intellij.testFramework.EdtTestUtil;
import dev.idank.r2d2.GitTest;
import dev.idank.r2d2.PluginLoader;
import dev.idank.r2d2.services.PluginLoaderService;
import dev.idank.r2d2.utils.PluginUtils;
import git4idea.repo.GitRepositoryManager;
import org.assertj.swing.annotation.GUITest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InvalidateCachesActionTest extends GitTest {

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
    @GUITest
    public void testInvalidateCaches() {
        myFixture.configureByText("test.txt", "test");

        PluginLoader pluginLoader = project.getService(PluginLoaderService.class).getPluginLoader();
        pluginLoader.onEnable(project, GitRepositoryManager.getInstance(project).getRepositories().stream().findFirst().orElseThrow());
        assertEquals(1, pluginLoader.getIssueData().size());

        PluginUtils.INSTANCE.updateGithubAccount(githubAccountManager, getDefaultGithubAccount(), PluginUtils.Action.REMOVE);

        EdtTestUtil.runInEdtAndWait(() -> {
            myFixture.testAction(new InvalidateCachesAction());
        });

        assertEquals(0, pluginLoader.getIssueData().size());
    }
}
