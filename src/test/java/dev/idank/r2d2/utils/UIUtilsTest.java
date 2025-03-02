package dev.idank.r2d2.utils;

import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;

public class UIUtilsTest {

    private FrameFixture window;

    @BeforeEach
    public void setUp() {
        JFrame frame = GuiActionRunner.execute(() -> new JFrame());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window = new FrameFixture(frame);
        window.show();
    }

    @AfterEach
    public void tearDown() {
        window.cleanUp();
    }

    @Test
    public void testShowError() {
        GuiActionRunner.execute(() -> UIUtils.showError("This is an error message", window.target()));

        window.optionPane().requireErrorMessage()
                .requireMessage("This is an error message")
                .okButton().click();
    }

    @Test
    public void testShowSuccess() {
        GuiActionRunner.execute(() -> UIUtils.showSuccess("This is a success message", window.target()));

        window.optionPane().requireInformationMessage()
                .requireMessage("This is a success message")
                .okButton().click();
    }

}
