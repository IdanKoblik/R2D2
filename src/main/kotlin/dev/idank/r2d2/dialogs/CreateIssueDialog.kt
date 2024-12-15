package dev.idank.r2d2.dialogs

import GitlabApiImpl
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.ui.DialogWrapper
import dev.idank.r2d2.api.Issue
import dev.idank.r2d2.utils.GitUtils
import dev.idank.r2d2.utils.GlabUtils
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.*
import javax.swing.text.JTextComponent

class CreateIssueDialog(project: Project, issueTitle: String) : DialogWrapper(project) {

    private val MAX_ISSUE_TITLE_LEN: Int = 255

    // TODO add a list of components
    private lateinit var issueTitleField: JTextField

    init {
        init()
        this.title = "Create GitLab Issue"
        this.issueTitleField.text = issueTitle
    }

    // TODO add component builder
    override fun createCenterPanel(): JComponent? {
        val panel = JPanel(GridBagLayout())
        val constraints = GridBagConstraints()

        val titleLabel = JLabel("Issue Title:")
        constraints.gridx = 0
        constraints.gridy = 0
        panel.add(titleLabel, constraints)

        this.issueTitleField = JTextField(20)
        constraints.gridx = 1
        constraints.gridy = 0
        panel.add(issueTitleField, constraints)

        return panel
    }

    override fun doOKAction() {
        val issueTitle = issueTitleField.text
        if (!assertComponent(issueTitleField))
            return

        if (issueTitle.length > MAX_ISSUE_TITLE_LEN) {
            JOptionPane.showMessageDialog(issueTitleField, "Title length cannot be greater then $MAX_ISSUE_TITLE_LEN", "Error", JOptionPane.ERROR_MESSAGE)
            return
        }

        val projectPath: String = ProjectManager.getInstance().openProjects[0].basePath!!
        val (hostname, namespace) = GitUtils.extractGitInfo(projectPath)

        if (hostname.lowercase() == "github.com") {
            JOptionPane.showMessageDialog(issueTitleField, "Github support not implemented yet", "Error", JOptionPane.ERROR_MESSAGE)
            return
        }

        val token = GlabUtils.getGitlabToken(hostname)
        if (token == null) {
            JOptionPane.showMessageDialog(issueTitleField, "Github support not implemented yet", "Error", JOptionPane.ERROR_MESSAGE)
            return
        }

        GitlabApiImpl(Issue.GitlabIssue(token, issueTitle, hostname, namespace)).createIssue()
        super.doOKAction()
    }

    private fun assertComponent(field: JTextComponent) : Boolean {
        if (field.text.isBlank()) {
            JOptionPane.showMessageDialog(issueTitleField, "%s cannot be empty.".format(field.name), "Error", JOptionPane.ERROR_MESSAGE)
            return false
        }

        return true
    }

}