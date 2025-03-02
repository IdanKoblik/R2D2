package dev.idank.r2d2.listeners;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.util.List;
import java.util.Vector;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SearchListenerTest {

    private SearchListener searchListener;

    @BeforeEach
    void setUp() {
        JPanel itemsPanel = new JPanel();
        JTextField searchField = new JTextField();
        Vector<String> allItems = new Vector<>(List.of("Apple", "Banana", "Cherry", "Date", "Elderberry"));

        searchListener = new SearchListener(itemsPanel, searchField, allItems);
    }

    @Test
    void testValidFilterItems() {
        Vector<String> items = searchListener.filterItems("App");
        assertEquals(1, items.size());
        assertEquals("Apple", items.firstElement());
    }

    @Test
    void testInvalidFilterItems() {
        Vector<String> items = searchListener.filterItems("Testing");
        assertTrue(items.isEmpty());
    }

}
