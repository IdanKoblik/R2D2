package dev.idank.r2d2.annotators;

import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.lang.annotation.HighlightSeverity;
import dev.idank.r2d2.BaseTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TODOAnnotatorTest extends BaseTest {

    @BeforeEach
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @AfterEach
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void testNormalCommentSingleLine() {
        myFixture.configureByFile("NormalCommentSingleLine.java");
        List<HighlightInfo> highlights = myFixture.doHighlighting();
        
        assertTrue(highlights.stream()
                .filter(highlight -> highlight.getDescription() != null)
                .anyMatch(highlight -> highlight.getDescription().equals("Create issue")
                        && highlight.getText().equals("TODO Add input validation")
                        && highlight.getSeverity() == HighlightSeverity.INFORMATION)
        );
    }

    @Test
    public void testMultiLineNormalComment() {
        myFixture.configureByFile("MultiLineNormalComment.java");
        List<HighlightInfo> highlights = myFixture.doHighlighting();

        assertTrue(highlights.stream()
                .filter(highlight -> highlight.getDescription() != null)
                .anyMatch(highlight -> highlight.getDescription().equals("Create issue")
                        && highlight.getText().contains("TODO Implement error handling")
                        && highlight.getSeverity() == HighlightSeverity.INFORMATION)
        );
    }

    @Test
    public void testBlockCommentSingleLine() {
        myFixture.configureByFile("BlockCommentSingleLine.java");
        List<HighlightInfo> highlights = myFixture.doHighlighting();

        assertTrue(highlights.stream()
                .filter(highlight -> highlight.getDescription() != null)
                .anyMatch(highlight -> highlight.getDescription().equals("Create issue")
                        && highlight.getText().contains("TODO Add authentication")
                        && highlight.getSeverity() == HighlightSeverity.INFORMATION)
        );
    }

    @Test
    public void testBlockCommentMultiLine() {
        myFixture.configureByFile("BlockCommentMultiLine.java");
        List<HighlightInfo> highlights = myFixture.doHighlighting();

        assertTrue(highlights.stream()
                .filter(highlight -> highlight.getDescription() != null)
                .anyMatch(highlight -> highlight.getDescription().equals("Create issue")
                        && highlight.getText().contains("TODO Implement caching")
                        && highlight.getSeverity() == HighlightSeverity.INFORMATION)
        );
    }

    @Test
    public void testJavadocCommentNotCounted() {
        myFixture.configureByFile("JavadocComment.java");
        List<HighlightInfo> highlights = myFixture.doHighlighting();

        assertFalse(highlights.stream()
                .filter(highlight -> highlight.getDescription() != null)
                .anyMatch(highlight -> highlight.getDescription().equals("Create issue")
                        && highlight.getText().contains("TODO"))
        );
    }

    @Test
    public void testMultipleTodos() {
        myFixture.configureByFile("MultipleTodos.java");
        List<HighlightInfo> highlights = myFixture.doHighlighting();

        String[] expectedTodos = {
                "TODO Implement feature A",
                "TODO Implement feature B"
        };

        for (String todo : expectedTodos) {
            assertTrue(highlights.stream()
                    .filter(highlight -> highlight.getDescription() != null)
                    .anyMatch(highlight -> highlight.getDescription().equals("Create issue")
                            && highlight.getText().contains(todo)
                            && highlight.getSeverity() == HighlightSeverity.INFORMATION)
            );
        }
    }

    @Test
    public void testKotlinTodoParameters() {
        myFixture.configureByFile("KotlinTodoParameters.kt");
        List<HighlightInfo> highlights = myFixture.doHighlighting();

        String[] expectedTodos = {
                "TODO(\"First todo\")",
                "TODO(reason = \"Third todo\")"
        };

        for (String todo : expectedTodos) {
            assertTrue(highlights.stream()
                    .filter(highlight -> highlight.getDescription() != null)
                    .anyMatch(highlight -> highlight.getDescription().equals("Create issue")
                            && highlight.getText().equals(todo)
                            && highlight.getSeverity() == HighlightSeverity.INFORMATION)
            );
        }
    }

}