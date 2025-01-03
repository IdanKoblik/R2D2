package dev.idank.r2d2.listeners;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.util.Vector;

public class SearchListener implements DocumentListener {

    private final JPanel itemsPanel;
    private final JTextField searchField;
    private final Vector<String> allItems;

    public SearchListener(JPanel itemsPanel, JTextField searchField, Vector<String> allItems) {
        this.itemsPanel = itemsPanel;
        this.searchField = searchField;
        this.allItems = allItems;
        this.searchField.getDocument().addDocumentListener(this);
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        updatePanel(filterItems(searchField.getText()));
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        updatePanel(filterItems(searchField.getText()));
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        updatePanel(filterItems(searchField.getText()));
    }

    private Vector<String> filterItems(String query) {
        Vector<String> filteredItems = new Vector<>();
        for (String item : allItems) {
            if (item.toLowerCase().contains(query.toLowerCase()))
                filteredItems.add(item);
        }

        return filteredItems;
    }

    public void updatePanel(Vector<String> filteredItems) {
        itemsPanel.removeAll();
        for (String item : filteredItems) {
            JCheckBox checkBox = new JCheckBox(item);
            itemsPanel.add(checkBox);
        }

        itemsPanel.revalidate();
        itemsPanel.repaint();
    }
}