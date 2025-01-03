package dev.idank.r2d2.utils;

import javax.swing.*;
import java.awt.*;

public class UIUtils {

    public static void showError(String message, Component parent) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void showSuccess(String message, Component parent) {
        JOptionPane.showMessageDialog(parent, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

}
