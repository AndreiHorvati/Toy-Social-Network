package com.example.toysocialnetworkgui;

import com.example.toysocialnetworkgui.controller.Controller;
import com.example.toysocialnetworkgui.model.Page;
import com.example.toysocialnetworkgui.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;

import java.io.IOException;

public class MainViewController {
    Controller controller;
    SceneController sceneController;

    @FXML
    AutoCompleteTextField searchBar;
    @FXML
    GridPane gridPane;

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void setSearchBarEntries() {
        searchBar.setEntries(controller.getUsersDetails());
    }

    private void loadMainView(FXMLLoader loader) {
        try {
            gridPane.getChildren().clear();
            gridPane.add(loader.load(), 0, 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onFriendRequestsButtonClick() {
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("Views/friend-requests-menu-view.fxml"));

        FriendRequestsMenuController friendRequestsMenuController;

        loadMainView(loader);

        friendRequestsMenuController = loader.getController();
        friendRequestsMenuController.setController(controller);
        friendRequestsMenuController.setMainViewController(this);
        friendRequestsMenuController.setFriendRequests();
    }

    public void loadProfile(Page page) {
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("Views/profile-view.fxml"));
        ProfileViewController profileViewController;

        loadMainView(loader);

        profileViewController = loader.getController();
        profileViewController.setController(controller);
        profileViewController.setMainViewController(this);
        profileViewController.setPage(page);
        profileViewController.initGUI();
        profileViewController.initFriendsList();
        profileViewController.setFriendButton();
    }

    private void handleSearchProfile() {
        String[] details = searchBar.getText().split(" - ");
        String username = details[1];

        User user = controller.getUserByUsername(username);

        if (user != null) {
            loadProfile(controller.getUserPage(user));
            searchBar.setText("");
        } else {
            System.out.println("nu exista useru");
        }
    }

    public void onKeyPressedTextField(KeyEvent key) {
        if (key.getCode().equals(KeyCode.ENTER)) {
            handleSearchProfile();
        }
    }

    public void setSceneController(SceneController sceneController) {
        this.sceneController = sceneController;
    }

    @FXML
    private void onLogoutButtonPressed() throws IOException {
        sceneController.changeToLoginScene();

        LoginViewController loginViewController = sceneController.getLoginViewController();

        loginViewController.setController(controller);
        loginViewController.setSceneController(sceneController);
    }
}
