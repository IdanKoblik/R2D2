package dev.idank.r2d2.dialogs;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vcs.configurable.VcsManagerConfigurable;
import com.intellij.util.ui.JBUI;
import dev.idank.r2d2.PluginLoader;
import dev.idank.r2d2.git.*;
import dev.idank.r2d2.git.data.GitUser;
import dev.idank.r2d2.git.data.Milestone;
import dev.idank.r2d2.git.data.User;
import dev.idank.r2d2.git.data.UserData;
import dev.idank.r2d2.git.request.GithubIssueRequest;
import dev.idank.r2d2.git.github.GithubService;
import dev.idank.r2d2.git.request.GitlabIssueRequest;
import dev.idank.r2d2.git.gitlab.GitlabService;
import dev.idank.r2d2.git.request.IssueRequest;
import dev.idank.r2d2.listeners.SearchListener;
import dev.idank.r2d2.utils.UIUtils;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
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

    private final JTextField titleField;
    private final JTextArea descriptionArea;
    private final JComboBox<String> accountCombo;

    private final JPanel labelPanel;
    private final JPanel assigneesPanel;

    private final JComboBox<String> milestoneCombo;

    public CreateIssueDialog(Project project, @NotNull String title, @NotNull String description, int lineNum, Document document) {
        super(project);
        this.project = project;
        this.lineNum = lineNum;
        this.document = document;

        titleField = new JTextField(TEXT_FIELD_WIDTH);
        titleField.setText(title);

        descriptionArea = new JTextArea(TEXT_AREA_HEIGHT, TEXT_FIELD_WIDTH);
        descriptionArea.setText(description);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);

        Vector<String> accounts = PluginLoader.getInstance().getGitAccounts();
        accounts.add(NO_USER);
        accountCombo = new JComboBox<>(accounts);

        Vector<String> milestones = PluginLoader.getInstance().getIssueData().milestones().stream()
                .map(milestone -> milestone.name() + " : " + milestone.id())
                .distinct().collect(Collectors.toCollection(Vector::new));

        milestones.add(NO_MILESTONE);
        milestoneCombo = new JComboBox<>(milestones);

        this.labelPanel = new JPanel();
        this.labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.Y_AXIS));

        this.assigneesPanel = new JPanel();
        this.assigneesPanel.setLayout(new BoxLayout(assigneesPanel, BoxLayout.Y_AXIS));

        init();
        setTitle("Create GitLab Issue");
    }

    @Override
    protected JComponent createCenterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = createConstraints();

        addTitleComponents(panel, constraints);
        addDescriptionComponents(panel, constraints);
        addGitComponents(panel, constraints);
        addLabelComponents(panel, constraints);
        addAssigneesComponents(panel, constraints);
        addMilestonesComponents(panel, constraints);

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

        constraints.gridx = 1;
        panel.add(titleField, constraints);
    }

    private void addDescriptionComponents(JPanel panel, GridBagConstraints constraints) {
        constraints.gridx = 0;
        constraints.gridy = 1;
        panel.add(new JLabel("Description:"), constraints);

        constraints.gridx = 1;
        panel.add(new JScrollPane(descriptionArea), constraints);
    }

    private void addGitComponents(JPanel panel, GridBagConstraints constraints) {
        constraints.gridx = 0;
        constraints.gridy = 2;
        panel.add(new JLabel("Git Account:"), constraints);

        constraints.gridx = 1;
        panel.add(accountCombo, constraints);
    }

    private void addMilestonesComponents(JPanel panel, GridBagConstraints constraints) {
        constraints.gridx = 1;
        constraints.gridy = 2;
        panel.add(new JLabel("Milestones:"), constraints);

        constraints.gridx = 2;
        panel.add(milestoneCombo, constraints);
    }

    private void addLabelComponents(JPanel panel, GridBagConstraints constraints) {
        constraints.gridx = 2;
        constraints.gridy = 0;
        panel.add(new JLabel("Labels:"), constraints);

        JTextField labelSearchField = new JTextField(TEXT_FIELD_WIDTH);

        constraints.gridx = 3;
        constraints.gridy = 0;
        panel.add(labelSearchField, constraints);

        JScrollPane labelScrollPane = new JScrollPane(labelPanel);
        labelScrollPane.setPreferredSize(new Dimension(150, 100));

        constraints.gridx = 2;
        constraints.gridy = 1;
        constraints.gridwidth = 2;
        panel.add(labelScrollPane, constraints);

        Vector<String> users = PluginLoader.getInstance().getIssueData().labels().stream()
                .distinct().collect(Collectors.toCollection(Vector::new));

        SearchListener searchListener = new SearchListener(labelPanel, labelSearchField, users);
        searchListener.updatePanel(users);
    }

    private void addAssigneesComponents(JPanel panel, GridBagConstraints constraints) {
        constraints.gridx = 4;
        constraints.gridy = 0;
        panel.add(new JLabel("Assignees:"), constraints);

        JTextField assigneesSearchField = new JTextField(TEXT_FIELD_WIDTH);

        constraints.gridx = 6;
        constraints.gridy = 0;
        panel.add(assigneesSearchField, constraints);

        JScrollPane assigneesScrollPane = new JScrollPane(assigneesPanel);
        assigneesScrollPane.setPreferredSize(new Dimension(150, 100));

        constraints.gridx = 4;
        constraints.gridy = 1;
        constraints.gridwidth = 4;
        panel.add(assigneesScrollPane, constraints);

        Vector<String> users = PluginLoader.getInstance().getIssueData().users().stream().map(user -> user.username() + " : " + user.id())
                .distinct().collect(Collectors.toCollection(Vector::new));

        SearchListener searchListener = new SearchListener(assigneesPanel, assigneesSearchField, users);
        searchListener.updatePanel(users);
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
                    VcsManagerConfigurable.APPLICATION_CONFIGURABLE.getName());
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
        GitUserExtractor userExtractor = GitUserExtractor.Companion.getInstance();
        Platform platform = user.platform();
        Map<Platform, UserData> users = userExtractor.extractUsers(project, user, platform);

        String milestone = null;
        if (!getSelectedMilestone().equals(NO_MILESTONE))
            milestone = getSelectedMilestone().trim().split(" : ")[1];

        if (platform == Platform.GITHUB) {
            GithubIssueRequest request = new GithubIssueRequest(
                    getTitle(),
                    getDescription(),
                    getSelectedItems(labelPanel),
                    getSelectedItems(assigneesPanel),
                    milestone
            );

            processIssueCreation(new GithubService(users.get(platform)), request, platform);
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

            processIssueCreation(new GitlabService(users.get(platform)), request, platform);
        }
    }

    private <T extends IssueRequest> void processIssueCreation(GitService<T> gitService, T request, Platform platform) {
        try (Response response = gitService.createIssue(request)) {
            if (!response.isSuccessful()) {
                UIUtils.showError("An error appeared while creating an issue", titleField);
                return;
            }

            String bodyString = response.body() != null ? response.body().string() : "";
            UIUtils.showSuccess("Successfully created an issue", titleField);

            if (lineNum >= 0) {
                updateDocument(bodyString, platform);
            }
        } catch (Exception e) {
            e.printStackTrace();
            UIUtils.showError("An error occurred while processing the request", titleField);
        }
    }

    private void updateDocument(String responseBody, Platform platform) throws Exception {
        JsonNode jsonArray = objectMapper.readTree(responseBody);
        int start = document.getLineStartOffset(lineNum);
        int end = document.getLineEndOffset(lineNum);
        String originalText = document.getText(new TextRange(start, end));
        String url = platform == Platform.GITLAB ?
                jsonArray.get("web_url").asText() :
                jsonArray.get("html_url").asText();

        WriteCommandAction.runWriteCommandAction(project, () -> {
            document.replaceString(start, end, originalText + " " + url);
        });
    }

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

}