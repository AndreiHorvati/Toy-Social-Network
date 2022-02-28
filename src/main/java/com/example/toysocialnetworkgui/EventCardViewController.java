package com.example.toysocialnetworkgui;

import com.example.toysocialnetworkgui.controller.Controller;
import com.example.toysocialnetworkgui.model.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class EventCardViewController {
    Event event;
    Controller controller;
    MainViewController mainViewController;

    @FXML
    Label nameLabel;
    @FXML
    Label dateLabel;
    @FXML
    Button subscribeButton;

    public void setMainViewController(MainViewController mainViewController) {
        this.mainViewController = mainViewController;
    }

    public void showEventsProfile() {
        mainViewController.loadEventPage(event);
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public void initGUI() {
        nameLabel.setText(event.getName());
        dateLabel.setText(event.getDate().getDayOfMonth() + "-" + event.getDate().getMonth().getValue()
        + "-" + event.getDate().getYear());

        if (event.isSubscribed(controller.getCurrentUser())) {
            subscribeButton.setText("Unsubscribe");
            subscribeButton.setStyle("-fx-border-radius: 10px;\n" +
                    "    -fx-background-radius: 10px;\n" +
                    "    -fx-text-fill: white;\n" +
                    "    -fx-border-width: 0.4;\n" +
                    "    -fx-border-color: white;\n" +
                    "    -fx-background-color: #391339;\n");
        }
    }

    @FXML
    private void onSubcscribeButtonClick() {
        if (event.isSubscribed(controller.getCurrentUser())) {
            subscribeButton.setText("Subscribe");
            subscribeButton.setStyle("-fx-border-radius: 10px;\n" +
                    "    -fx-background-radius: 10px;\n" +
                    "    -fx-text-fill: white;\n" +
                    "    -fx-border-width: 0.4;\n" +
                    "    -fx-border-color: white;\n" +
                    "    -fx-background-color: #2e2eb8;\n");

            controller.unsbscribeToEvent(controller.getCurrentUser(), event);
            event.unsubscribe(controller.getCurrentUser());
        } else {
            subscribeButton.setText("Unsubscribe");
            subscribeButton.setStyle("-fx-border-radius: 10px;\n" +
                    "    -fx-background-radius: 10px;\n" +
                    "    -fx-text-fill: white;\n" +
                    "    -fx-border-width: 0.4;\n" +
                    "    -fx-border-color: white;\n" +
                    "    -fx-background-color: #391339;\n");

            controller.subscribeToEvent(controller.getCurrentUser(), event);
            event.subscribe(controller.getCurrentUser());
        }
    }
}
