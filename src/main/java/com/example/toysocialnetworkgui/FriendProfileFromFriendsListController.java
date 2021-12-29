package com.example.toysocialnetworkgui;

import com.example.toysocialnetworkgui.controller.Controller;
import com.example.toysocialnetworkgui.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class FriendProfileFromFriendsListController {
    Controller controller;
    User user;
    MainViewController mainViewController;

    @FXML
    Label nameLabel;
    @FXML
    ImageView profilePicture;

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void setUser(User user) {
        this.user = user;
    }

    private void initNameLabel() {
        this.nameLabel.setText(user.getFirstName() + "\n" + user.getLastName());
    }

    private void initProfilePicture() {
        String profilePictureName = controller.getProfilePicture(user.getUsername());

        if(profilePictureName != null) {
            Image image = new Image(String.valueOf(HelloApplication.class.getResource("images/" + profilePictureName)));
            profilePicture.setImage(image);
        }
    }

    public void initGUI() {
        initNameLabel();
        initProfilePicture();
    }

    public void onFriendProfileButtonPressed(MouseEvent mouseEvent) {
        mainViewController.loadProfile(controller.getUserPage(this.user));
    }

    public void setMainViewController(MainViewController mainViewController) {
        this.mainViewController = mainViewController;
    }
}
