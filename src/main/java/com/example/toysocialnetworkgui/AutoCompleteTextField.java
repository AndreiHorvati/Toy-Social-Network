package com.example.toysocialnetworkgui;

import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.*;

public class AutoCompleteTextField extends TextField {
    private final SortedSet<String> entries;
    private final ContextMenu entriesPopup;

    public AutoCompleteTextField() {
        super();
        entries = new TreeSet<>();

        entriesPopup = new ContextMenu();

        textProperty().addListener((observableValue, oldValues, newValue) -> {
            if (getText().length() == 0) {
                entriesPopup.hide();
            } else {
                LinkedList<String> searchResult = new LinkedList<>();

                String[] input = entries.toArray(new String[entries.size()]);
                for (int i = 0; i < input.length; i++) {
                    if (input[i].toUpperCase().contains(getText().toUpperCase())) {
                        searchResult.add(input[i]);
                    }
                }

                if (entries.size() > 0) {
                    populatePopup(searchResult);

                    if (!entriesPopup.isShowing()) {
                        entriesPopup.show(AutoCompleteTextField.this, Side.BOTTOM, 0, 0);
                    }
                } else {
                    entriesPopup.hide();
                }
            }
        });

        focusedProperty().addListener((observable, oldValue, newValue) -> entriesPopup.hide());
    }

    private void populatePopup(List<String> searchResult) {
        List<CustomMenuItem> menuItems = new LinkedList<>();

        int maxEntries = 10;
        int count = Math.min(searchResult.size(), maxEntries);

        for (int i = 0; i < count; i++) {
            final String result = searchResult.get(i);
            Label entryLabel = new Label(result);
            CustomMenuItem item = new CustomMenuItem(entryLabel, true);

            item.setOnAction(event -> {
                setText(result);
                entriesPopup.hide();
            });

            menuItems.add(item);
        }

        entriesPopup.getItems().clear();
        entriesPopup.getItems().addAll(menuItems);
    }

    public SortedSet<String> getEntries() {
        return entries;
    }

    public void setEntries(List<String> entries) {
        getEntries().addAll(entries);
    }
}

