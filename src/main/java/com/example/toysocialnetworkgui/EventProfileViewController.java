package com.example.toysocialnetworkgui;

import com.example.toysocialnetworkgui.controller.Controller;
import com.example.toysocialnetworkgui.model.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.io.IOException;

public class EventProfileViewController {
    Event event;
    Controller controller;
    MainViewController mainViewController;

    @FXML
    Label creatorLabel;
    @FXML
    Label nameLabel;
    @FXML
    Label dateLabel;
    @FXML
    Label locationLabel;
    @FXML
    Label descriptionLabel;
    @FXML
    GridPane participantsList;
    @FXML
    Button subscribeButton;

    int row, column;

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void setMainViewController(MainViewController mainViewController) {
        this.mainViewController = mainViewController;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public void initParticipantsList() {
        row = column = 0;

        participantsList.getChildren().clear();
        participantsList.getRowConstraints().clear();
        participantsList.getColumnConstraints().clear();

        event.getParticipants().forEach(participant -> {
            try {
                FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("Views/friend-profile-from-friends-list-view.fxml"));
                FriendProfileFromFriendsListController friendProfileFromFriendsListController;

                participantsList.add(loader.load(), column, row);

                friendProfileFromFriendsListController = loader.getController();
                friendProfileFromFriendsListController.setController(controller);
                friendProfileFromFriendsListController.setMainViewController(mainViewController);
                friendProfileFromFriendsListController.setUser(participant);
                friendProfileFromFriendsListController.initGUI();

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

        initParticipantsList();
    }

    public void initGUI() {
        this.creatorLabel.setText("@" + event.getCreator().getUsername());
        this.nameLabel.setText(event.getName());
        this.locationLabel.setText(event.getLocation());
        dateLabel.setText(event.getDate().getDayOfMonth() + "-" + event.getDate().getMonth().getValue()
                + "-" + event.getDate().getYear());
        this.descriptionLabel.setText(event.getDescription());

        initParticipantsList();

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
}
