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
        userDataOpt.ifPresent(userData -> {
            dialog.setData(PluginLoader.getInstance().getIssueData().get(userData));
        });
    }
}