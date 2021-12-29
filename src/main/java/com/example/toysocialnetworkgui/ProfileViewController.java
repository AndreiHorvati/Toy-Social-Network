package com.example.toysocialnetworkgui;

import com.example.toysocialnetworkgui.controller.Controller;
import com.example.toysocialnetworkgui.model.Friendship;
import com.example.toysocialnetworkgui.model.Page;
import com.example.toysocialnetworkgui.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import java.io.IOException;

public class ProfileViewController {
    Controller controller;
    Page page;
    MainViewController mainViewController;

    @FXML
    Label nameLabel;
    @FXML
    GridPane friendsList;
    @FXML
    Button friendButton;
    @FXML
    ImageView profilePicture;
    @FXML
    Label usernameLabel;

    int row, column;

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    private void initNameLabel() {
        User user = page.getUser();

        nameLabel.setText(user.getFirstName() + " " + user.getLastName());
    }

    private void initProfilePicture() {
        User user = page.getUser();
        String profilePictureName = controller.getProfilePicture(user.getUsername());

        if (profilePictureName != null) {
            Image image = new Image(String.valueOf(HelloApplication.class.getResource("images/" + profilePictureName)));
            profilePicture.setImage(image);
        }
    }

    private void initUsernameLabel() {
        User user = page.getUser();

        usernameLabel.setText("@" + user.getUsername());
    }

    public void initGUI() {
        initNameLabel();
        initProfilePicture();
        initUsernameLabel();
    }

    public void initFriendsList() {
        row = column = 0;

        page.getFriends().forEach(friend -> {
            try {
                FriendProfileFromFriendsListController friendProfileFromFriendsListController;
                FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("Views/friend-profile-from-friends-list-view.fxml"));

                friendsList.add(loader.load(), column, row);

                friendProfileFromFriendsListController = loader.getController();
                friendProfileFromFriendsListController.setController(controller);
                friendProfileFromFriendsListController.setMainViewController(mainViewController);
                friendProfileFromFriendsListController.setUser(friend);
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

    public void setMainViewController(MainViewController mainViewController) {
        this.mainViewController = mainViewController;
    }

    private void setSendFriendRequestButton() {
        friendButton.setText("Add friend");
        friendButton.setOnAction(event -> {
            onSendFriendRequestButtonClick();
            mainViewController.loadProfile(page);
        });
    }

    private void onSendFriendRequestButtonClick() {
        User user = page.getUser();
        String username = user.getUsername();
        this.controller.sendFriendRequestFromCurrentUser(username);
    }

    private void onRemoveFriendButtonClick() {
        User user = page.getUser();
        String username = user.getUsername();
        this.controller.deleteFriendFromCurrentUser(username);
    }

    private void setRemoveFriendButton() {
        friendButton.setText("Unfriend");
        friendButton.setOnAction(event -> {
            onRemoveFriendButtonClick();
            mainViewController.loadProfile(page);
        });
    }

    private void onRemoveFriendRequestButton() {
        User user = page.getUser();
        String username = user.getUsername();
        this.controller.deleteFriendRequestFromCurrentUser(username);
    }

    private void setRemoveFriendRequestButton() {
        friendButton.setText("Remove Request");
        friendButton.setOnAction(event -> {
            onRemoveFriendRequestButton();
            mainViewController.loadProfile(page);
        });
    }

    private void onAcceptFriendRequestButton() {
        User user = page.getUser();
        String username = user.getUsername();
        controller.approveFriendRequestFromCurrentUser(username);
    }

    private void setAcceptFriendRequestButton() {
        friendButton.setText("Accept");
        friendButton.setOnAction(event -> {
            onAcceptFriendRequestButton();
            mainViewController.loadProfile(page);
        });
    }

    public void setRejectedFriendButton() {
        this.friendButton.setStyle("-fx-background-color: #660000");
        this.friendButton.setDisable(true);
        this.friendButton.setText("Rejected");
    }

    public void setFriendButton() {
        User user = page.getUser();
        String username = user.getUsername();
        Friendship friendship = controller.getFriendshipBetweenCurrentUserAndAnotherUser(username);

        if (friendship == null) {
            setSendFriendRequestButton();
        } else if (friendship.getStatus().equals("approved")) {
            setRemoveFriendButton();
        } else if (friendship.getStatus().equals("pending") &&
                friendship.getId().getLeft().equals(controller.getCurrentUser().getId())) {
            setRemoveFriendRequestButton();
        } else if (friendship.getStatus().equals("pending") &&
                friendship.getId().getRight().equals(controller.getCurrentUser().getId())) {
            setAcceptFriendRequestButton();
        } else if (friendship.getStatus().equals("rejected")) {
            setRejectedFriendButton();
        }
    }
}
