package com.example.toysocialnetworkgui;

import com.example.toysocialnetworkgui.controller.Controller;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class CreateEventViewController {
    @FXML
    Spinner<Integer> hourSpinner;
    @FXML
    Spinner<Integer> minuteSpinner;
    @FXML
    TextField nameTextfield;
    @FXML
    TextField locationTextField;
    @FXML
    DatePicker datePicker;
    @FXML
    TextField descriptionTextField;

    Controller controller;
    Stage dialogStage;

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void createEvent() {
        String name = nameTextfield.getText();
        String description = descriptionTextField.getText();
        String location = locationTextField.getText();
        LocalDate date = datePicker.getValue();
        int hour = hourSpinner.getValue();
        int minute = minuteSpinner.getValue();

        LocalDateTime dateTime = LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfYear(), hour, minute);

        controller.saveEvent(controller.getCurrentUser(), name, description, location, dateTime);

        dialogStage.close();
    }

    public void setDialog(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
}
