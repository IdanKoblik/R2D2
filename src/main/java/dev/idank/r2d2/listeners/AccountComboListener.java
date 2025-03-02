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

package dev.idank.r2d2.listeners;

import dev.idank.r2d2.PluginLoader;
import dev.idank.r2d2.dialogs.CreateIssueDialog;
import dev.idank.r2d2.git.data.AuthData;
import dev.idank.r2d2.git.data.GitUser;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Optional;

public class AccountComboListener implements ActionListener {

    private final CreateIssueDialog dialog;
    private final PluginLoader pluginLoader;

    public AccountComboListener(CreateIssueDialog dialog, PluginLoader pluginLoader) {
        this.dialog = dialog;
        this.pluginLoader = pluginLoader;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        GitUser gitUser = this.pluginLoader.createGitUser(dialog.getSelectedAccount());
        if (gitUser == null)
            return;

        Optional<AuthData> userDataOpt = this.pluginLoader.getUserManager().getUserData(gitUser);
        userDataOpt.ifPresent(userData -> dialog.setData(this.pluginLoader.getIssueData().get(userData)));
    }
}