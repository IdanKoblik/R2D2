package dev.idank.r2d2.actions;

import com.intellij.testFramework.EditorTestUtil;
import com.intellij.testFramework.EdtTestUtil;
import dev.idank.r2d2.BaseTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InvalidateCachesActionTest extends BaseTest {

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
    void testExec() {
        EdtTestUtil.runInEdtAndWait(() -> {
            EditorTestUtil.executeAction(myFixture.getEditor(), "dev.idank.r2d2.actions.InvalidateCachesAction", true);
        });
    }
}
