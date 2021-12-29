package com.example.toysocialnetworkgui;

import com.example.toysocialnetworkgui.controller.Controller;
import com.example.toysocialnetworkgui.model.FriendRequestDTO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class FriendRequestViewController {
    Controller controller;
    FriendRequestDTO friendRequestDTO;
    FriendRequestsMenuController friendRequestsMenuController;
    MainViewController mainViewController;

    @FXML
    Label nameLabel;
    @FXML
    ImageView profilePicture;

    public void setFriendRequestDTO(FriendRequestDTO friendRequestDTO) {
        this.friendRequestDTO = friendRequestDTO;
    }

    private void initNameLabel() {
        this.nameLabel.setText(friendRequestDTO.getFriendFirstName() + " " + friendRequestDTO.getFriendLastName());

    }

    private void initProfilePicture() {
        String profilePictureName = controller.getProfilePicture(friendRequestDTO.getUsername());

        if (profilePictureName != null) {
            Image image = new Image(String.valueOf(HelloApplication.class.getResource("images/" + profilePictureName)));
            profilePicture.setImage(image);
        }
    }

    public void initGUI() {
        initNameLabel();
        initProfilePicture();
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void onAcceptButtonClick() {
        String username = friendRequestDTO.getUsername();

        this.controller.approveFriendRequestFromCurrentUser(username);
        this.friendRequestsMenuController.setFriendRequests();
    }

    public void onRejectButtonClick() {
        String username = friendRequestDTO.getUsername();

        this.controller.rejectFriendRequestFromCurrentUser(username);
        this.friendRequestsMenuController.setFriendRequests();
    }

    public void setMenuController(FriendRequestsMenuController menuController) {
        this.friendRequestsMenuController = menuController;
    }

    public void setMainViewController(MainViewController mainViewController) {
        this.mainViewController = mainViewController;
    }

    public void onProfilePictureClick() {
        String username = friendRequestDTO.getUsername();
        mainViewController.loadProfile(controller.getUserPage(controller.getUserByUsername(username)));
    }
}
