package com.example.toysocialnetworkgui;

import com.example.toysocialnetworkgui.controller.Controller;
import javafx.fxml.FXML;

public class MainViewController {
    Controller controller;

    @FXML
    AutoCompleteTextField searchBar;

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void setSearchBarEntries() {
        searchBar.setEntries(controller.getFullNames());
    }
}
