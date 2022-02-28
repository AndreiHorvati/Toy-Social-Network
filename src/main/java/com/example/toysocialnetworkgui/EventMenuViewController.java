package com.example.toysocialnetworkgui;

import com.example.toysocialnetworkgui.controller.Controller;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.GridPane;

import java.io.IOException;

public class EventMenuViewController {
    @FXML
    GridPane eventsGridPane;

    Controller controller;
    MainViewController mainViewController;

    int row, column;

    public void setMainViewController(MainViewController mainViewController) {
        this.mainViewController = mainViewController;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void initGUI() {
        initEvents();
    }

    private void initEvents() {
        row = column = 0;

        eventsGridPane.getChildren().clear();
        eventsGridPane.getRowConstraints().clear();
        eventsGridPane.getColumnConstraints().clear();

        controller.getAllEvents().forEach(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("Views/event-card-view.fxml"));
                EventCardViewController eventCardViewController;

                eventsGridPane.add(loader.load(), column, row);

                eventCardViewController = loader.getController();
                eventCardViewController.setController(controller);
                eventCardViewController.setEvent(event);
                eventCardViewController.initGUI();
                eventCardViewController.setMainViewController(mainViewController);

                column++;
                if (column == 3) {
                    column = 0;
                    row++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
