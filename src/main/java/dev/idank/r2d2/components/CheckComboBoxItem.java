package dev.idank.r2d2.components;

public class CheckComboBoxItem {
    private final String text;
    private boolean selected;

    public CheckComboBoxItem(String text) {
        this.text = text;
        this.selected = false;
    }

    public String getText() {
        return text;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public String toString() {
        return text;
    }
}
