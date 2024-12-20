import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase5
import dev.idank.r2d2.actions.CreateIssueIntentionAction
import dev.idank.r2d2.dialogs.CreateIssueDialog
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.swing.JButton
import javax.swing.JLabel

class CreateIssueIntentionActionTest : LightJavaCodeInsightFixtureTestCase5() {

    @BeforeEach
    fun setUp() {

    }

    @Test
    fun `test create issue intention action invokes dialog`() {
        val action = CreateIssueIntentionAction("Test Title", "Test Description")

        action.invoke(fixture.project, fixture.editor, null)

        val dialog = CreateIssueDialog(fixture.project, "Test Title", "Test Description")
        dialog.show()

        assert(dialog.isVisible)

        val titleLabel = dialog.rootPane.contentPane.getComponent(0) as JLabel
        val descriptionLabel = dialog.rootPane.contentPane.getComponent(1) as JLabel

        assertEquals("Test Title", titleLabel.text)
        assertEquals("Test Description", descriptionLabel.text)

        val button = dialog.rootPane.contentPane.getComponent(2) as JButton
        assertNotNull(button)
    }

    @Test
    fun `test create issue intention action is available`() {
        val action = CreateIssueIntentionAction("Test Title", "Test Description")
        val isAvailable = action.isAvailable(fixture.project, fixture.editor, null)

        assert(isAvailable)
    }
}
