package com.example.toysocialnetworkgui;

import com.example.toysocialnetworkgui.controller.Controller;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.GridPane;

import java.io.IOException;

public class FriendRequestsMenuController {
    Controller controller;
    MainViewController mainViewController;

    int row, column;

    @FXML
    GridPane gridPane;

    public FriendRequestsMenuController() {
        row = column = 0;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    private void clearGridPane() {
        this.gridPane.getRowConstraints().clear();
        this.gridPane.getColumnConstraints().clear();
        this.gridPane.getChildren().clear();

        this.row = this.column = 0;
    }

    private void populateGridPane() {
        controller.getAllFriendRequestsFromCurrentUser().forEach(friendRequestDTO -> {
            try {
                FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("Views/friend-request-view.fxml"));
                gridPane.add(loader.load(), column, row);

                column++;
                if (column == 6) {
                    column = 0;
                    row++;
                }

                FriendRequestViewController friendRequestViewController = loader.getController();
                friendRequestViewController.setFriendRequestDTO(friendRequestDTO);
                friendRequestViewController.setController(controller);
                friendRequestViewController.setMenuController(this);
                friendRequestViewController.initGUI();
                friendRequestViewController.setMainViewController(mainViewController);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void setFriendRequests() {
        clearGridPane();
        populateGridPane();
    }

    public void setMainViewController(MainViewController mainViewController) {
        this.mainViewController = mainViewController;
    }
}
