package dev.idank.r2d2.dialogs;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import dev.idank.r2d2.git.GitUserExtractor;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;

public class CreateIssueDialog extends DialogWrapper {

    private static final int MAX_ISSUE_TITLE_LEN = 255;

    private final Project project;
    private JTextField issueTitleField;
    private JTextArea descriptionArea;

    public CreateIssueDialog(Project project, String title, String description) {
        super(project);
        this.project = project;

        init();
        setTitle("Create GitLab Issue");
        issueTitleField.setText(title);
        if (description != null) {
            descriptionArea.setText(description);
        }
    }

    @Override
    protected JComponent createCenterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        JLabel titleLabel = new JLabel("Issue Title:");
        constraints.gridx = 0;
        constraints.gridy = 0;
        panel.add(titleLabel, constraints);

        issueTitleField = new JTextField(20);
        constraints.gridx = 1;
        constraints.gridy = 0;
        panel.add(issueTitleField, constraints);

        JLabel descriptionLabel = new JLabel("Description:");
        constraints.gridx = 0;
        constraints.gridy = 1;
        panel.add(descriptionLabel, constraints);

        descriptionArea = new JTextArea(5, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        constraints.gridx = 1;
        constraints.gridy = 1;
        panel.add(scrollPane, constraints);

        return panel;
    }

    @Override
    protected void doOKAction() {
        String issueTitle = issueTitleField.getText();
        if (!assertComponent(issueTitleField)) {
            return;
        }

        if (issueTitle.length() > MAX_ISSUE_TITLE_LEN) {
            JOptionPane.showMessageDialog(issueTitleField,
                    "Title length cannot be greater than " + MAX_ISSUE_TITLE_LEN,
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        GitUserExtractor.getInstance().extractUsers(project);

        super.doOKAction();
    }

    private boolean assertComponent(JTextComponent field) {
        if (field.getText().isBlank()) {
            JOptionPane.showMessageDialog(issueTitleField,
                    field.getName() + " cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }
}