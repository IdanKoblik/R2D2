package dev.idank.r2d2.actions;

import com.intellij.testFramework.EditorTestUtil;
import com.intellij.testFramework.EdtTestUtil;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import dev.idank.r2d2.git.GitUserExtractor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class InvalidateCachesActionTest extends BasePlatformTestCase {

    private GitUserExtractor instance;

    @BeforeEach
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        GitUserExtractor.Companion.resetInstance();
        instance = GitUserExtractor.Companion.getInstance();
    }

    @AfterEach
    @Override
    protected void tearDown() throws Exception {
        GitUserExtractor.Companion.resetInstance();
        super.tearDown();
    }

    @Test
    public void testInvalidateCaches() {
        instance.setLastCacheTime(0);

        EdtTestUtil.runInEdtAndWait(() -> {
            myFixture.configureByFile("DummyJava.java");
            EditorTestUtil.executeAction(myFixture.getEditor(), "dev.idank.r2d2.actions.InvalidateCachesAction", true);
        });

        assertEquals(-1, instance.getLastCacheTime());
    }

    @Override
    protected String getTestDataPath() {
        return "src/test/testData";
    }

}