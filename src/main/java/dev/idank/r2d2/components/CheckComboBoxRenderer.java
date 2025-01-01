package dev.idank.r2d2.components;

import javax.swing.*;
import java.awt.*;

public class CheckComboBoxRenderer implements ListCellRenderer<CheckComboBoxItem> {
    private final JCheckBox checkBox;

    public CheckComboBoxRenderer() {
        checkBox = new JCheckBox();
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends CheckComboBoxItem> list, CheckComboBoxItem value, int index, boolean isSelected, boolean cellHasFocus) {
        checkBox.setText(value.getText());
        checkBox.setSelected(value.isSelected());
        return checkBox;
    }
}