package dev.idank.r2d2.annotators;

import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.lang.annotation.HighlightSeverity;
import dev.idank.r2d2.AnnotatorTest;
import dev.idank.r2d2.BaseTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@AnnotatorTest
class TODOAnnotatorTest extends BaseTest {

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
    void testNormalCommentSingleLine() {
        getMyFixture().configureByFile("NormalCommentSingleLine.java");
        List<HighlightInfo> highlights = getMyFixture().doHighlighting();
        
        assertTrue(highlights.stream()
                .filter(highlight -> highlight.getDescription() != null)
                .anyMatch(highlight -> highlight.getDescription().equals("Create issue")
                        && highlight.getText().equals("TODO Add input validation")
                        && highlight.getSeverity() == HighlightSeverity.INFORMATION)
        );
    }

    @Test
    void testMultiLineNormalComment() {
        getMyFixture().configureByFile("MultiLineNormalComment.java");
        List<HighlightInfo> highlights = getMyFixture().doHighlighting();

        assertTrue(highlights.stream()
                .filter(highlight -> highlight.getDescription() != null)
                .anyMatch(highlight -> highlight.getDescription().equals("Create issue")
                        && highlight.getText().contains("TODO Implement error handling")
                        && highlight.getSeverity() == HighlightSeverity.INFORMATION)
        );
    }

    @Test
    void testBlockCommentSingleLine() {
        getMyFixture().configureByFile("BlockCommentSingleLine.java");
        List<HighlightInfo> highlights = getMyFixture().doHighlighting();

        assertTrue(highlights.stream()
                .filter(highlight -> highlight.getDescription() != null)
                .anyMatch(highlight -> highlight.getDescription().equals("Create issue")
                        && highlight.getText().contains("TODO Add authentication")
                        && highlight.getSeverity() == HighlightSeverity.INFORMATION)
        );
    }

    @Test
    void testBlockCommentMultiLine() {
        getMyFixture().configureByFile("BlockCommentMultiLine.java");
        List<HighlightInfo> highlights = getMyFixture().doHighlighting();

        assertTrue(highlights.stream()
                .filter(highlight -> highlight.getDescription() != null)
                .anyMatch(highlight -> highlight.getDescription().equals("Create issue")
                        && highlight.getText().contains("TODO Implement caching")
                        && highlight.getSeverity() == HighlightSeverity.INFORMATION)
        );
    }

    @Test
    void testJavadocCommentNotCounted() {
        getMyFixture().configureByFile("JavadocComment.java");
        List<HighlightInfo> highlights = getMyFixture().doHighlighting();

        assertFalse(highlights.stream()
                .filter(highlight -> highlight.getDescription() != null)
                .anyMatch(highlight -> highlight.getDescription().equals("Create issue")
                        && highlight.getText().contains("TODO"))
        );
    }

    @Test
    void testMultipleTodos() {
        getMyFixture().configureByFile("MultipleTodos.java");
        List<HighlightInfo> highlights = getMyFixture().doHighlighting();

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
    void testKotlinTodoParameters() {
        getMyFixture().configureByFile("KotlinTodoParameters.kt");
        List<HighlightInfo> highlights = getMyFixture().doHighlighting();

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