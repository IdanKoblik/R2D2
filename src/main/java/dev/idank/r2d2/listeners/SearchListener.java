/*
 * MIT License
 *
 * Copyright (c) 2025 Idan Koblik
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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

    public Vector<String> filterItems(String query) {
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