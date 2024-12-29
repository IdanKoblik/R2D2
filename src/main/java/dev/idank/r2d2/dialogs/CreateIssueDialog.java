package dev.idank.r2d2.dialogs;

import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vcs.configurable.VcsManagerConfigurable;
import com.intellij.util.ui.JBUI;
import dev.idank.r2d2.git.*;
import dev.idank.r2d2.utils.GitUtils;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.github.authentication.GHAccountsUtil;
import org.jetbrains.plugins.github.authentication.accounts.GithubAccount;
import org.jetbrains.plugins.gitlab.authentication.accounts.GitLabAccount;
import org.jetbrains.plugins.gitlab.authentication.accounts.PersistentGitLabAccountManager;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.util.Map;
import java.util.Vector;

public class CreateIssueDialog extends DialogWrapper {

    private static final int MAX_ISSUE_TITLE_LEN = 255;
    private static final int TEXT_FIELD_WIDTH = 20;
    private static final int TEXT_AREA_HEIGHT = 5;
    private static final String NO_USER = "No user";

    private final Project project;
    private JTextField issueTitleField;
    private JTextArea descriptionArea;
    private JComboBox<String> accountCombo;

    public CreateIssueDialog(Project project, @NotNull String title, @NotNull String description) {
        super(project);
        this.project = project;

        init();
        setTitle("Create GitLab Issue");
        setupInitialValues(title, description);
    }

    private void setupInitialValues(String title, String description) {
        issueTitleField.setText(title);
        descriptionArea.setText(description);
    }

    @Override
    protected JComponent createCenterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = createConstraints();

        addTitleComponents(panel, constraints);
        addDescriptionComponents(panel, constraints);
        addGitComponents(panel, constraints);

        return panel;
    }

    private GridBagConstraints createConstraints() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = JBUI.insets(5);
        return constraints;
    }

    private void addTitleComponents(JPanel panel, GridBagConstraints constraints) {
        constraints.gridx = 0;
        constraints.gridy = 0;
        panel.add(new JLabel("Issue Title:"), constraints);

        issueTitleField = new JTextField(TEXT_FIELD_WIDTH);
        constraints.gridx = 1;
        panel.add(issueTitleField, constraints);
    }

    private void addDescriptionComponents(JPanel panel, GridBagConstraints constraints) {
        constraints.gridx = 0;
        constraints.gridy = 1;
        panel.add(new JLabel("Description:"), constraints);

        descriptionArea = new JTextArea(TEXT_AREA_HEIGHT, TEXT_FIELD_WIDTH);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);

        constraints.gridx = 1;
        panel.add(new JScrollPane(descriptionArea), constraints);
    }

    private void addGitComponents(JPanel panel, GridBagConstraints constraints) {
        constraints.gridx = 0;
        constraints.gridy = 2;
        panel.add(new JLabel("Git Account:"), constraints);

        Vector<String> accounts = new Vector<>();
        Vector<String> githubAccounts = getGitHubAccounts();
        Vector<String> gitLabAccounts = getGitLabAccounts();

        accounts.addAll(githubAccounts);
        accounts.addAll(gitLabAccounts);
        accounts.add(NO_USER);
        accountCombo = new JComboBox<>(accounts);

        constraints.gridx = 1;
        panel.add(accountCombo, constraints);
    }

    private Vector<String> getGitHubAccounts() {
        Vector<String> accounts = new Vector<>();
        if (githubAccountsSize() == 0)
            return accounts;

        for (GithubAccount account : GHAccountsUtil.getAccounts()) {
            String serverUrl = account.getServer().toString();
            if (GitUtils.extractGitInfo(ProjectManager.getInstance().getOpenProjects()[0].getBasePath(), this)
                    .containsKey(serverUrl)) {
                accounts.add(account.getName() + " / " + account.getServer().getSchema() +"://" + account.getServer() + " / " + Platform.GITHUB.getName());
            }
        }

        return accounts;
    }

    private Vector<String> getGitLabAccounts() {
        Vector<String> accounts = new Vector<>();
        if (gitlabAccountsSize() == 0)
            return accounts;

        PersistentGitLabAccountManager accountManager = new PersistentGitLabAccountManager();
        for (GitLabAccount account : accountManager.getAccountsState().getValue()) {
            String server = account.getServer().toString();
            String cleanServer = server.replace("https://", "").replace("http://", "");

            if (GitUtils.extractGitInfo(ProjectManager.getInstance().getOpenProjects()[0].getBasePath(), this)
                    .containsKey(cleanServer)) {
                accounts.add(account.getName() + " / " + server + " / " + Platform.GITLAB.getName());
            }
        }

        return accounts;
    }

    @Override
    protected void doOKAction() {
        if (!validateUserSelection())
            return;

        if (!validateTitle())
            return;

        GitUser git = createGitHubUser();
        createIssues(git);
        super.doOKAction();
    }

    private boolean validateUserSelection() {
        if (getSelectedAccount().equals(NO_USER)) {
            showError("You must have at least one git user connected to idea");
            ShowSettingsUtil.getInstance().showSettingsDialog(project, VcsManagerConfigurable.APPLICATION_CONFIGURABLE.getName());
            return false;
        }

        return true;
    }

    private boolean validateTitle() {
        if (!assertComponent(issueTitleField))
            return false;

        if (issueTitleField.getText().length() > MAX_ISSUE_TITLE_LEN) {
            showError("Title length cannot be greater than " + MAX_ISSUE_TITLE_LEN);
            return false;
        }

        return true;
    }

    private GitUser createGitHubUser() {
        if (getSelectedAccount().equals(NO_USER))
            return null;

        String[] account = getSelectedAccount().split(" / ");
        GitInfo gitInfo = GitUtils.extractGitInfo(ProjectManager.getInstance().getOpenProjects()[0].getBasePath(), this).get(
                account[1].replace("https://", "").replace("http://", "")
        );

        return new GitUser(account[0], account[1], gitInfo.namespace(), gitInfo.url(), Platform.fromName(account[2]));
    }

    private void createIssues(GitUser user) {
        GitUserExtractor userExtractor = GitUserExtractor.Companion.getInstance();
        Map<Platform, UserData> users = userExtractor.extractUsers(project, user, user.platform());

        if (user.platform().equals(Platform.GITHUB))
            createIssueForPlatform(new GithubIssueService(users.get(Platform.GITHUB)));
        else if (user.platform().equals(Platform.GITLAB))
            createIssueForPlatform(new GitlabIssueService(users.get(Platform.GITLAB)));
    }

    private void createIssueForPlatform(IssueService issueService) {
        IssueCreator issueCreator = new IssueCreator(issueService);
        Response response = issueCreator.createIssue(issueTitleField.getText(), descriptionArea.getText());
        if (response.isSuccessful())
            showSuccess("Successfully created an issue");
        else
            showError("An error appeared while creating an issue");
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(issueTitleField, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(issueTitleField, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private boolean assertComponent(JTextComponent field) {
        if (field.getText().isBlank()) {
            showError(field.getName() + " cannot be empty.");
            return false;
        }
        return true;
    }

    private int gitlabAccountsSize() {
        return new PersistentGitLabAccountManager().getAccountsState().getValue().size();
    }

    private int githubAccountsSize() {
        return GHAccountsUtil.getAccounts().size();
    }

    private String getSelectedAccount() {
        return (String) accountCombo.getSelectedItem();
    }

}