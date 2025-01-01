package dev.idank.r2d2.listeners;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.util.Vector;
import java.util.stream.Collectors;

public class LabelSearchListener implements DocumentListener {

    private final JPanel panel;
    private final JTextField labelSearchField;
    private final Vector<String> allLabels;

    public LabelSearchListener(JPanel panel, JTextField labelSearchField, Vector<String> allLabels) {
        this.panel = panel;
        this.labelSearchField = labelSearchField;
        this.allLabels = allLabels;
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        filterLabels();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        filterLabels();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        filterLabels();
    }

    private void filterLabels() {
        String searchText = labelSearchField.getText().toLowerCase();
        Vector<String> filteredLabels = allLabels.stream()
                .filter(label -> label.toLowerCase().contains(searchText))
                .collect(Collectors.toCollection(Vector::new));
        updateLabelPanel(filteredLabels);
    }

    public void updateLabelPanel(Vector<String> labels) {
        panel.removeAll();
        for (String label : labels) {
            JCheckBox checkBox = new JCheckBox(label);
            panel.add(checkBox);
        }
        panel.revalidate();
        panel.repaint();
    }
}
