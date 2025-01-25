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
package dev.idank.r2d2.dialogs;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.TextRange;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.JBUI;
import dev.idank.r2d2.PluginLoader;
import dev.idank.r2d2.git.Platform;
import dev.idank.r2d2.git.api.GitService;
import dev.idank.r2d2.git.api.GithubService;
import dev.idank.r2d2.git.api.GitlabService;
import dev.idank.r2d2.git.data.GitUser;
import dev.idank.r2d2.git.data.IssueData;
import dev.idank.r2d2.git.data.Milestone;
import dev.idank.r2d2.git.data.UserData;
import dev.idank.r2d2.git.request.GithubIssueRequest;
import dev.idank.r2d2.git.request.GitlabIssueRequest;
import dev.idank.r2d2.git.request.IssueRequest;
import dev.idank.r2d2.listeners.AccountComboListener;
import dev.idank.r2d2.listeners.SearchListener;
import dev.idank.r2d2.managers.UserManager;
import dev.idank.r2d2.utils.UIUtils;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.stream.Collectors;

public class CreateIssueDialog extends DialogWrapper {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public static final int MAX_ISSUE_TITLE_LEN = 255;
    public static final int TEXT_FIELD_WIDTH = 20;
    public static final int TEXT_AREA_HEIGHT = 5;
    public static final String NO_USER = "No user";
    public static final String NO_MILESTONE = "No milestone";

    private final Project project;
    private final int lineNum;
    private final Document document;
    private final String title;
    private final String description;
    private final Vector<String> accounts;

    private JTextField titleField;
    private JTextArea descriptionArea;
    private JComboBox<String> accountCombo;

    private JPanel labelPanel;
    private JPanel assigneesPanel;

    private JComboBox<String> milestoneCombo;
    private final Set<Component> components = new HashSet<>();

    private IssueData data = null;
    private final JPanel panel;
    private final GridBagConstraints gbc;

    public CreateIssueDialog(Project project, @NotNull String title, @NotNull String description, int lineNum, Document document) {
        super(project);
        this.project = project;
        this.lineNum = lineNum;
        this.document = document;
        this.title = title;
        this.description = description;

        this.panel = new JPanel(new GridBagLayout());
        this.gbc = new GridBagConstraints();

        this.accounts = new Vector<>(PluginLoader.getInstance().getGitAccounts());
        String firstUser = accounts.firstElement();
        Optional<GitUser> gitUserOpt = UserManager.getInstance().getUser(firstUser);
        if (gitUserOpt.isEmpty())
            return;

        Optional<UserData> userDataOpt = UserManager.getInstance().getUserData(gitUserOpt.get());
        if (userDataOpt.isEmpty())
            return;

        setData(
                PluginLoader.getInstance().getIssueData().get(userDataOpt.get()),
                false
        );

        init();
        setTitle("Create GitLab Issue");
    }

    @Override
    protected JComponent createCenterPanel() {
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = JBUI.insets(5);
        gbc.weightx = 1.0;

        JPanel leftPanel = new JPanel(new GridBagLayout());
        GridBagConstraints leftGbc = new GridBagConstraints();
        leftGbc.fill = GridBagConstraints.BOTH;
        leftGbc.insets = JBUI.insets(5);
        leftGbc.weightx = 1.0;

        leftGbc.gridx = 0;
        leftGbc.gridy = 0;
        leftGbc.gridwidth = 1;
        leftGbc.weighty = 0;
        leftPanel.add(new JLabel("Issue Title:"), leftGbc);

        leftGbc.gridx = 1;
        leftGbc.weightx = 1.0;
        titleField = new JTextField(TEXT_FIELD_WIDTH);
        titleField.setText(title);
        leftPanel.add(titleField, leftGbc);

        leftGbc.gridx = 0;
        leftGbc.gridy = 1;
        leftGbc.weighty = 0;
        leftGbc.weightx = 0;
        leftPanel.add(new JLabel("Description:"), leftGbc);

        leftGbc.gridx = 1;
        leftGbc.weightx = 1.0;
        leftGbc.weighty = 1.0;
        descriptionArea = new JTextArea(TEXT_AREA_HEIGHT, TEXT_FIELD_WIDTH);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setText(description);
        JBScrollPane descScrollPane = new JBScrollPane(descriptionArea);
        leftPanel.add(descScrollPane, leftGbc);

        leftGbc.gridx = 0;
        leftGbc.gridy = 2;
        leftGbc.weighty = 0;
        leftGbc.weightx = 0;
        leftPanel.add(new JLabel("Git Account:"), leftGbc);

        leftGbc.gridx = 1;
        leftGbc.weightx = 1.0;
        accountCombo = new JComboBox<>(accounts);
        accountCombo.addActionListener(new AccountComboListener(this));
        leftPanel.add(accountCombo, leftGbc);

        leftGbc.gridx = 0;
        leftGbc.gridy = 3;
        leftGbc.weightx = 0;
        leftPanel.add(new JLabel("Milestones:"), leftGbc);

        leftGbc.gridx = 1;
        leftGbc.weightx = 1.0;
        leftPanel.add(milestoneCombo, leftGbc);

        JPanel rightPanel = new JPanel(new GridBagLayout());
        GridBagConstraints rightGbc = new GridBagConstraints();
        rightGbc.fill = GridBagConstraints.BOTH;
        rightGbc.insets = JBUI.insets(5);

        rightGbc.gridx = 0;
        rightGbc.gridy = 0;
        rightGbc.gridwidth = 1;
        rightGbc.weightx = 0;
        rightPanel.add(new JLabel("Labels:"), rightGbc);

        rightGbc.gridx = 1;
        rightGbc.weightx = 1.0;
        JTextField labelSearchField = new JTextField(TEXT_FIELD_WIDTH);
        rightPanel.add(labelSearchField, rightGbc);

        rightGbc.gridx = 0;
        rightGbc.gridy = 1;
        rightGbc.gridwidth = 2;
        rightGbc.weighty = 1.0;
        JScrollPane labelScrollPane = new JBScrollPane(labelPanel);
        labelScrollPane.setPreferredSize(new Dimension(200, 150));
        rightPanel.add(labelScrollPane, rightGbc);

        rightGbc.gridx = 0;
        rightGbc.gridy = 2;
        rightGbc.gridwidth = 1;
        rightGbc.weighty = 0;
        rightGbc.weightx = 0;
        rightPanel.add(new JLabel("Assignees:"), rightGbc);

        rightGbc.gridx = 1;
        rightGbc.weightx = 1.0;
        JTextField assigneesSearchField = new JTextField(TEXT_FIELD_WIDTH);
        rightPanel.add(assigneesSearchField, rightGbc);

        rightGbc.gridx = 0;
        rightGbc.gridy = 3;
        rightGbc.gridwidth = 2;
        rightGbc.weighty = 1.0;
        JScrollPane assigneesScrollPane = new JBScrollPane(assigneesPanel);
        assigneesScrollPane.setPreferredSize(new Dimension(200, 150));
        rightPanel.add(assigneesScrollPane, rightGbc);

        Vector<String> labelItems = data.labels().stream()
                .distinct()
                .collect(Collectors.toCollection(Vector::new));
        new SearchListener(labelPanel, labelSearchField, labelItems).updatePanel(labelItems);

        Vector<String> userItems = data.users().stream()
                .map(user -> user.username() + " : " + user.id())
                .distinct()
                .collect(Collectors.toCollection(Vector::new));
        new SearchListener(assigneesPanel, assigneesSearchField, userItems).updatePanel(userItems);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.5;
        gbc.weighty = 1.0;
        panel.add(leftPanel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.5;
        panel.add(rightPanel, gbc);

        components.clear();
        components.addAll(Arrays.asList(titleField, descriptionArea, accountCombo, milestoneCombo,
                labelPanel, assigneesPanel, labelScrollPane, assigneesScrollPane));

        panel.setMinimumSize(new Dimension(800, 600));

        return panel;
    }

    @Override
    protected void doOKAction() {
        if (!validateInput())
            return;

        GitUser git = PluginLoader.getInstance().createGitUser(getSelectedAccount());
        createIssue(git);

        super.doOKAction();
    }

    private boolean validateInput() {
        if (getSelectedAccount().equals(NO_USER)) {
            UIUtils.showError("You must have at least one git user connected to idea", titleField);
            ShowSettingsUtil.getInstance().showSettingsDialog(project,
                    Configurable.APPLICATION_CONFIGURABLE.getName());
            return false;
        }

        return validateTitle();
    }

    private boolean validateTitle() {
        String title = getTitle();
        if (title.isBlank()) {
            UIUtils.showError("Title cannot be empty.", titleField);
            return false;
        }

        if (title.length() > MAX_ISSUE_TITLE_LEN) {
            UIUtils.showError("Title length cannot be greater than " + MAX_ISSUE_TITLE_LEN, titleField);
            return false;
        }

        return true;
    }

    private void createIssue(GitUser user) {
        String milestone = null;
        if (!getSelectedMilestone().equals(NO_MILESTONE))
            milestone = getSelectedMilestone().trim().split(" : ")[1];

        Optional<UserData> userDataOpt = UserManager.getInstance().getUserData(user);
        if (userDataOpt.isEmpty())
            return;

        UserData userData = userDataOpt.get();
        Platform platform = user.platform();
        if (platform == Platform.GITHUB) {
            GithubIssueRequest request = new GithubIssueRequest(
                    getTitle(),
                    getDescription(),
                    getSelectedItems(labelPanel),
                    getSelectedItems(assigneesPanel).stream().map(
                            item -> item.trim().split(" : ")[0]
                    ).collect(Collectors.toSet()),
                    milestone
            );

            processIssueCreation(new GithubService(userData), request, platform);
        } else if (platform == Platform.GITLAB) {
            GitlabIssueRequest request = new GitlabIssueRequest(
                    getTitle(),
                    getDescription(),
                    getSelectedItems(labelPanel),
                    getSelectedItems(assigneesPanel).stream().map(
                            item -> Integer.parseInt(item.trim().split(" : ")[1])
                    ).collect(Collectors.toSet()),
                    milestone
            );

            processIssueCreation(new GitlabService(userData), request, platform);
        }
    }

    private <T extends IssueRequest> void processIssueCreation(GitService<T> gitService, T request, Platform platform) {
        try (Response response = gitService.createIssue(request)) {
            if (!response.isSuccessful()) {
                UIUtils.showError("An error appeared while creating an issue: " + response.code() + "\n" + "Target: " + response.request().url(), titleField);
                return;
            }

            String bodyString = response.body() != null ? response.body().string() : "";
            UIUtils.showSuccess("Successfully created an issue", titleField);

            if (lineNum >= 0)
                updateDocument(bodyString, platform);
        } catch (Exception e) {
            UIUtils.showError("An error occurred while processing the request", titleField);
        }
    }

    private void updateDocument(String responseBody, Platform platform) {
        try {
            JsonNode jsonArray = objectMapper.readTree(responseBody);
            int start = document.getLineStartOffset(lineNum);
            int end = document.getLineEndOffset(lineNum);
            String originalText = document.getText(new TextRange(start, end));

            String url = "";
            if (platform == Platform.GITLAB) {
                url = jsonArray.get("web_url").asText();
                if (url == null || url.isEmpty()) {
                    UIUtils.showError("Missing 'web_url' in response body.", titleField);
                    return;
                }
            } else if (platform == Platform.GITHUB) {
                url = jsonArray.get("html_url").asText();
                if (url == null || url.isEmpty()) {
                    UIUtils.showError("Missing 'html_url' in response body.", titleField);
                    return;
                }
            }

            String finalUrl = url;
            WriteCommandAction.runWriteCommandAction(project, () -> document.replaceString(start, end, originalText + " " + finalUrl));
        } catch (Exception e) {
            UIUtils.showError("Error updating document text.\n" + e.getMessage(), titleField);
        }
    }

    @Override
    public String getTitle() {
        return titleField.getText();
    }

    public String getDescription() {
        return descriptionArea.getText();
    }

    public String getSelectedAccount() {
        return (String) accountCombo.getSelectedItem();
    }

    public String getSelectedMilestone() {
        return (String) milestoneCombo.getSelectedItem();
    }

    public Set<String> getSelectedItems(JPanel panel) {
        Set<String> selected = new HashSet<>();
        for (Component comp : panel.getComponents()) {
            if (comp instanceof JCheckBox checkbox && checkbox.isSelected()) {
                selected.add(checkbox.getText());
            }
        }

        return selected;
    }

    public Set<Component> getComponents() {
        return Collections.unmodifiableSet(this.components);
    }

    public void setData(IssueData data) {
        setData(data, true);
    }

    public void setData(IssueData data, boolean flag) {
        IssueData old = this.data;
        if (data == old)
            return;

        this.data = data;
        Vector<String> milestones;
        Set<Milestone> availableMilestones = data.milestones();

        milestones = availableMilestones.stream()
                .map(milestone -> milestone.name() + " : " + milestone.id())
                .distinct()
                .collect(Collectors.toCollection(Vector::new));

        milestones.add(NO_MILESTONE);
        milestoneCombo = new JComboBox<>(milestones);

        this.labelPanel = new JPanel();
        this.labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.Y_AXIS));

        this.assigneesPanel = new JPanel();
        this.assigneesPanel.setLayout(new BoxLayout(assigneesPanel, BoxLayout.Y_AXIS));

        if (flag)
            refreshDialogContent(getSelectedAccount());
    }

    private void refreshDialogContent(String lastAccount) {
        panel.removeAll();
        components.clear();
        createCenterPanel();

        panel.revalidate();
        panel.repaint();
        pack();

        this.accountCombo.setSelectedItem(lastAccount);
        SwingUtilities.invokeLater(() -> {
            panel.revalidate();
            panel.repaint();
            getContentPane().revalidate();
            getContentPane().repaint();
        });
    }
}