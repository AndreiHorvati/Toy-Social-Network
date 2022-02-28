package com.example.toysocialnetworkgui;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;

public class ReplyMenuViewController {

    @FXML
    ImageView replyImage;
    boolean visible;

    public void initGUI() {
        makeReplyImageInvisible();
    }

    public void makeReplyImageVisible() {
        replyImage.setVisible(true);
        visible = true;
    }

    public void makeReplyImageInvisible() {
        replyImage.setVisible(false);
        visible = false;
    }

    public boolean isReplyImageVisible() {
        return visible;
    }
}
