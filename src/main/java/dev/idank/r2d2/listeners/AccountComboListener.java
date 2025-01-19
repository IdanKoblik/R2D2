/*
MIT License

Copyright (c) 2025 Idan Koblik

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */
package dev.idank.r2d2.listeners;

import dev.idank.r2d2.PluginLoader;
import dev.idank.r2d2.dialogs.CreateIssueDialog;
import dev.idank.r2d2.git.data.GitUser;
import dev.idank.r2d2.git.data.UserData;
import dev.idank.r2d2.managers.UserManager;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Optional;

public class AccountComboListener implements ActionListener {

    private final CreateIssueDialog dialog;

    public AccountComboListener(CreateIssueDialog dialog) {
        this.dialog = dialog;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        GitUser gitUser = PluginLoader.getInstance().createGitUser(dialog.getSelectedAccount());
        if (gitUser == null)
            return;

        Optional<UserData> userDataOpt = UserManager.getInstance().getUserData(gitUser);
        userDataOpt.ifPresent(userData -> dialog.setData(PluginLoader.getInstance().getIssueData().get(userData)));
    }
}