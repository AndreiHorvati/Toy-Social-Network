package com.example.toysocialnetworkgui;

import com.example.toysocialnetworkgui.controller.Controller;
import com.example.toysocialnetworkgui.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ProfileFromChatListViewController {
    private ChatViewController chatViewController;
    private Controller controller;
    private User user;

    @FXML
    Label nameLabel;
    @FXML
    ImageView profilePicture;

    public void setChatViewController(ChatViewController chatViewController) {
        this.chatViewController = chatViewController;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    private void initProfilePicture() {
        String profilePictureName = controller.getProfilePicture(user.getUsername());

        if (profilePictureName != null) {
            Image image = new Image(String.valueOf(HelloApplication.class.getResource("images/" + profilePictureName)));
            profilePicture.setImage(image);
        }
    }

    private void initNameLabel() {
        this.nameLabel.setText(user.getFirstName() + " " + user.getLastName());
    }

    public void initGUI() {
        initNameLabel();
        initProfilePicture();
    }

    public void changeUser() {
        this.chatViewController.setUser(user);
        chatViewController.initConversation();
        chatViewController.setAfterUserDetails();
    }
}
