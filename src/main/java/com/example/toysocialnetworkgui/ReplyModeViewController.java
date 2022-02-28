package com.example.toysocialnetworkgui;

import com.example.toysocialnetworkgui.model.Message;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ReplyModeViewController {
    @FXML
    Label nameLabel;
    @FXML
    Label messageLabel;

    ChatViewController chatViewController;

    Message message;

    public void setChatViewController(ChatViewController chatViewController) {
        this.chatViewController = chatViewController;
    }

    public void closeReplyMode() {
        this.chatViewController.closeReplyMode();
        System.out.println("asd");
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public void setGUI() {
        this.nameLabel.setText(message.getFrom().getFirstName() + " " + message.getFrom().getLastName());
        this.messageLabel.setText(message.getMessage());
    }
}
