package com.example.toysocialnetworkgui;

import com.example.toysocialnetworkgui.controller.Controller;
import com.example.toysocialnetworkgui.model.Event;
import com.example.toysocialnetworkgui.model.Page;
import com.example.toysocialnetworkgui.model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class MainViewController {
    Controller controller;
    SceneController sceneController;

    @FXML
    AutoCompleteTextField searchBar;
    @FXML
    GridPane gridPane;
    @FXML
    ImageView exclamationImage;
    @FXML
    VBox notificationVBox;
    private NotificationsThread notificationThread;

    public void makeExclamationImageVisible() {
        this.exclamationImage.setVisible(true);
    }

    public void makeNotificationVBoxVisible() {
        this.notificationVBox.setVisible(true);
    }

    public void makeNotificationVBoxInvisible() {
        this.notificationVBox.setVisible(false);
    }

    public void clearNotifications() {
        this.notificationVBox.getChildren().clear();
    }

    public void addNotification(String notificationText) {
        Label label = new Label();

        label.setText(notificationText);
        label.setStyle(
                """
                        -fx-background-color: #1a1a1a;
                        -fx-border-radius: 5px;
                        -fx-background-radius: 5px;
                        -fx-font-size: 16px;
                        -fx-padding: 5px;
                        -fx-text-fill: white;
                        -fx-border-color: linear-gradient(to right top, #0c0537, #1c0c44, #2f0d51, #440c5c, #590766);
                        -fx-border-width: 0.6;
                        """.indent(4));

        notificationVBox.getChildren().add(label);
    }

    public void makeExclamationImageInvisible() {
        this.exclamationImage.setVisible(false);
    }

    public void setNotifications() {
        if (exclamationImage.isVisible()) {
            makeExclamationImageInvisible();
            makeNotificationVBoxVisible();
        } else if (notificationVBox.isVisible()) {
            makeNotificationVBoxInvisible();
            clearNotifications();
        }
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void setSearchBarEntries() {
        searchBar.setEntries(controller.getUsersDetails());
    }

    public void loadMainView(FXMLLoader loader) {
        try {
            gridPane.getChildren().clear();
            gridPane.add(loader.load(), 0, 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onChatButtonPressed() {
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("Views/chat-view.fxml"));
        ChatViewController chatViewController;

        loadMainView(loader);

        chatViewController = loader.getController();
        chatViewController.setController(controller);
        chatViewController.initGUI();

        List<User> usersForConversations = controller.getAllUsersForConversations(controller.getCurrentUser());

        if (usersForConversations.size() > 0) {
            chatViewController.setUser(usersForConversations.get(0));
            chatViewController.initConversation();
            chatViewController.initScrollPane();
            chatViewController.setAfterUserDetails();
        }
        chatViewController.setSearchBarEntries();

        controller.addObserver(chatViewController);
    }

    public void onEventsButtonPressed() {
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("Views/events-menu-view.fxml"));
        EventMenuViewController eventMenuViewController;

        loadMainView(loader);

        eventMenuViewController = loader.getController();
        eventMenuViewController.setController(controller);
        eventMenuViewController.setMainViewController(this);
        eventMenuViewController.initGUI();
    }

    public void onReportsButtonPressed() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(HelloApplication.class.getResource("Views/reports-menu-view.fxml"));

            AnchorPane root = (AnchorPane) loader.load();
            Stage dialogStage = new Stage();
            Scene scene = new Scene(root);

            dialogStage.setScene(scene);
            dialogStage.show();

            ReportsMenuViewController reportsMenuViewController = loader.getController();
            reportsMenuViewController.setController(controller);
            reportsMenuViewController.setStage(dialogStage);
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

    public void loadEventPage(Event event) {
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("Views/event-profile-view.fxml"));
        EventProfileViewController eventProfileViewController;

        loadMainView(loader);

        eventProfileViewController = loader.getController();
        eventProfileViewController.setController(controller);
        eventProfileViewController.setMainViewController(this);
        eventProfileViewController.setEvent(event);
        eventProfileViewController.initGUI();
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

        notificationThread.stop();

        loginViewController.setNotificationThread(new NotificationsThread(controller));
    }

    public void setNotificationThread(NotificationsThread notificationThread) {
        this.notificationThread = notificationThread;
    }
}
