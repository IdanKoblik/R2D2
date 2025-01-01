package dev.idank.r2d2.dialogs;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import dev.idank.r2d2.git.data.UserData;
import dev.idank.r2d2.git.request.GithubIssueRequest;
import dev.idank.r2d2.git.github.GithubService;
import dev.idank.r2d2.git.request.GitlabIssueRequest;
import dev.idank.r2d2.git.gitlab.GitlabService;
import dev.idank.r2d2.git.request.IssueRequest;
import dev.idank.r2d2.listeners.LabelSearchListener;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

public class CreateIssueDialog extends DialogWrapper {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final int MAX_ISSUE_TITLE_LEN = 255;
    private static final int TEXT_FIELD_WIDTH = 20;
    private static final int TEXT_AREA_HEIGHT = 5;
    public static final String NO_USER = "No user";

    private final Project project;
    private final int lineNum;
    private final Document document;
    private final LabelSearchListener labelSearchListener;
    private final JPanel labelPanel;

    private JTextField issueTitleField;
    private JTextArea descriptionArea;
    private JComboBox<String> accountCombo;
    private JTextField labelSearchField;
    private Vector<String> allLabels;

    public CreateIssueDialog(Project project, @NotNull String title, @NotNull String description, int lineNum, Document document) {
        super(project);
        this.project = project;
        this.lineNum = lineNum;
        this.document = document;

        this.labelPanel = new JPanel();
        this.labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.Y_AXIS));
        this.labelSearchListener = new LabelSearchListener(labelPanel, labelSearchField, allLabels);

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
        addLabelComponents(panel, constraints);

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

        Vector<String> accounts = PluginLoader.getInstance().getGitAccounts();
        accounts.add(NO_USER);
        accountCombo = new JComboBox<>(accounts);

        constraints.gridx = 1;
        panel.add(accountCombo, constraints);
    }

    private void addLabelComponents(JPanel panel, GridBagConstraints constraints) {
        constraints.gridx = 2;
        constraints.gridy = 0;
        constraints.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("Labels:"), constraints);

        labelSearchField = new JTextField(TEXT_FIELD_WIDTH);
        labelSearchField.getDocument().addDocumentListener(labelSearchListener);

        constraints.gridx = 3;
        constraints.gridy = 0;
        panel.add(labelSearchField, constraints);

        JScrollPane labelScrollPane = new JScrollPane(labelPanel);
        labelScrollPane.setPreferredSize(new Dimension(150, 100));

        constraints.gridx = 2;
        constraints.gridy = 1;
        constraints.gridwidth = 2;
        constraints.fill = GridBagConstraints.BOTH;
        panel.add(labelScrollPane, constraints);

        populateLabels();
    }

    private void populateLabels() {
        allLabels = new Vector<>(PluginLoader.getInstance().getIssueData().labels());
        labelSearchListener.updateLabelPanel(allLabels);
    }

    @Override
    protected void doOKAction() {
        if (!validateUserSelection())
            return;

        if (!validateTitle())
            return;

        GitUser git = PluginLoader.getInstance().createGitUser(getSelectedAccount());
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

    private void createIssues(GitUser user) {
        GitUserExtractor userExtractor = GitUserExtractor.Companion.getInstance();
        Map<Platform, UserData> users = userExtractor.extractUsers(project, user, user.platform());

        if (user.platform().equals(Platform.GITHUB))
            createIssueForPlatform(new GithubService(users.get(Platform.GITHUB)), user.platform(), new GithubIssueRequest(
                    issueTitleField.getText(),
                    descriptionArea.getText(),
                    getSelectedLabels()
            ));
        else if (user.platform().equals(Platform.GITLAB))
            createIssueForPlatform(new GitlabService(users.get(Platform.GITLAB)), user.platform(), new GitlabIssueRequest(
                    issueTitleField.getText(),
                    descriptionArea.getText(),
                    getSelectedLabels()
            ));
    }

    private <T extends IssueRequest> void createIssueForPlatform(GitService<T> gitService, Platform platform, T data) {
        try (Response response = gitService.createIssue(data)) {
            if (response.isSuccessful()) {
                ResponseBody responseBody = response.body();
                String bodyString = responseBody != null ? responseBody.string() : "";
                response.close();

                showSuccess("Successfully created an issue");

                if (lineNum != -1) {
                    int start = document.getLineStartOffset(lineNum);
                    int end = document.getLineEndOffset(lineNum);

                    try {
                        JsonNode jsonArray = objectMapper.readTree(bodyString);
                        String originalText = document.getText(new TextRange(start, end));

                        WriteCommandAction.runWriteCommandAction(project, () -> {
                            String newText = String.format("%s %s", originalText, (platform == Platform.GITLAB ? jsonArray.get("web_url") : jsonArray.get("html_url")));
                            document.replaceString(start, end, newText);
                        });
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                        showError("Failed to process the response from the server");
                    }
                }
            } else {
                showError("An error appeared while creating an issue");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("An error occurred while processing the request");
        }
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(issueTitleField, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void showSuccess(String message) {
        JOptionPane.showMessageDialog(issueTitleField, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private boolean assertComponent(JTextComponent field) {
        if (field.getText().isBlank()) {
            showError(field.getName() + " cannot be empty.");
            return false;
        }

        return true;
    }

    private String getSelectedAccount() {
        return (String) accountCombo.getSelectedItem();
    }

    private Set<String> getSelectedLabels() {
        Set<String> selectedLabels = new HashSet<>();
        for (Component comp : labelPanel.getComponents()) {
            if (comp instanceof JCheckBox checkBox) {
                if (checkBox.isSelected()) {
                    selectedLabels.add(checkBox.getText());
                }
            }
        }

        return selectedLabels;
    }
}
