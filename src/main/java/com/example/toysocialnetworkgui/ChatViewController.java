package com.example.toysocialnetworkgui;

import com.example.toysocialnetworkgui.controller.Controller;
import com.example.toysocialnetworkgui.model.Message;
import com.example.toysocialnetworkgui.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.Node;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class ChatViewController implements Observer {
    public ImageView userDetailHeaderProfilePicture;
    public Label userDetailHeaderNameLabel;
    public Label userDetailHeaderUsernameLabel;
    private Controller controller;
    private User user;

    boolean replyMode;
    Message messageToReply;

    @FXML
    VBox chatList;
    @FXML
    GridPane chatGridPane;
    @FXML
    TextField chatTextField;
    @FXML
    ScrollPane scrollPane;
    @FXML
    VBox chatVBox;
    @FXML
    ScrollPane chatScrollPane;

    @FXML
    AutoCompleteTextField searchBar;

    int row;

    public ChatViewController() {
        this.user = null;
        this.row = 0;
        replyMode = false;
        messageToReply = null;
    }

    public void initScrollPane() {
        chatScrollPane.setVvalue(1D);
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void setSearchBarEntries() {
        searchBar.setEntries(controller.getUsersDetails());
    }

    public void changeUser(User user) {
        this.setUser(user);
        this.initConversation();
        this.setAfterUserDetails();
    }

    private void handleSearchPerson() {
        String[] details = searchBar.getText().split(" - ");
        String username = details[1];

        User user = controller.getUserByUsername(username);

        if (user != null) {
            changeUser(user);
            searchBar.setText("");
        } else {
            System.out.println("nu exista useru");
        }
    }

    public void onKeyPressedTextField(KeyEvent key) {
        if (key.getCode().equals(KeyCode.ENTER)) {
            handleSearchPerson();
        }
    }

    private void initChatList() {
        chatList.getChildren().clear();

        controller.getAllUsersForConversations(controller.getCurrentUser()).forEach(user -> {
            try {
                ProfileFromChatListViewController profileFromChatListViewController;
                FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("Views/profile-from-chat-list-view.fxml"));

                chatList.getChildren().add(loader.load());

                profileFromChatListViewController = loader.getController();
                profileFromChatListViewController.setController(controller);
                profileFromChatListViewController.setUser(user);
                profileFromChatListViewController.setChatViewController(this);
                profileFromChatListViewController.initGUI();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void setAfterUserDetails() {
        String profilePictureName = controller.getProfilePicture(user.getUsername());

        System.out.println(profilePictureName);

        if (profilePictureName != null) {
            Image image = new Image(String.valueOf(HelloApplication.class.getResource("images/" + profilePictureName)));
            userDetailHeaderProfilePicture.setImage(image);
        } else {
            Image image = new Image(String.valueOf(HelloApplication.class.getResource("images/profile-2398782_960_720.png")));
            userDetailHeaderProfilePicture.setImage(image);
        }

        userDetailHeaderNameLabel.setText(user.getFirstName() + " " + user.getLastName());
        userDetailHeaderUsernameLabel.setText("@" + user.getUsername());
    }

    public void initGUI() {
        initChatList();
    }

    public void clearGridPane() {
        this.chatGridPane.getChildren().clear();
        this.chatGridPane.getRowConstraints().clear();
    }

    public void placeMessageLabel(Label messageLabel, User sender, Message reply, Message message) {
        HBox hBox = new HBox();
        VBox vBox = new VBox();
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("Views/reply-menu-view.fxml"));

        Node replyMenu = null;
        try {
            replyMenu = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        hBox.setSpacing(5);

        int column;
        if (controller.verifyIfUserIsCurrentUser(sender)) {
            hBox.getChildren().add(replyMenu);
            hBox.getChildren().add(messageLabel);
            hBox.setAlignment(Pos.CENTER_RIGHT);

            vBox.setAlignment(Pos.CENTER_RIGHT);

            column = 2;
        } else {
            hBox.getChildren().add(messageLabel);
            hBox.getChildren().add(replyMenu);
            hBox.setAlignment(Pos.CENTER_LEFT);

            vBox.setAlignment(Pos.CENTER_LEFT);

            column = 0;
        }

        if(reply != null) {
            Label replyMessageLabel = createReplyMessageLabel(reply.getMessage());
            vBox.getChildren().add(replyMessageLabel);

            vBox.setSpacing(-20);
        }

        ReplyMenuViewController replyMenuViewController = loader.getController();
        replyMenuViewController.initGUI();

        vBox.getChildren().add(hBox);

        this.chatGridPane.add(vBox, column, row);
        row++;

        messageLabel.setOnMouseEntered(event -> {
            replyMenuViewController.makeReplyImageVisible();
        });

        messageLabel.setOnMouseExited(event -> {
            if (!controller.verifyIfUserIsCurrentUser(sender)) {
                if (event.getX() <= messageLabel.getLayoutX() + messageLabel.getWidth()) {
                    replyMenuViewController.makeReplyImageInvisible();
                }
            } else {
                if (event.getX() >= hBox.getLayoutX()) {
                    replyMenuViewController.makeReplyImageInvisible();
                }
            }
        });

        replyMenu.setOnMouseExited(event -> {
            if (replyMenuViewController.isReplyImageVisible()) {
                replyMenuViewController.makeReplyImageInvisible();
            }
        });

        replyMenu.setOnMouseClicked(event -> changeToReplyMode(message));

        chatGridPane.heightProperty().addListener((observable, oldValue, newValue) -> chatScrollPane.setVvalue((Double) newValue));
        chatTextField.setText("");
    }

    public void changeToReplyMode(Message messageToReply) {
        replyMode = true;
        this.messageToReply = messageToReply;

        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("Views/replying-mode-view.fxml"));

        try {
            int height = 228;

            chatGridPane.setMaxHeight(height);
            chatGridPane.prefHeight(height);

            chatScrollPane.setMaxHeight(height);
            chatScrollPane.prefHeight(height);

            chatVBox.getChildren().add(3, loader.load());

            ReplyModeViewController replyModeViewController = loader.getController();
            replyModeViewController.setChatViewController(this);
            replyModeViewController.setMessage(messageToReply);
            replyModeViewController.setGUI();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeReplyMode() {
        replyMode = false;
        this.messageToReply = null;

        this.chatVBox.getChildren().remove(3);

        chatGridPane.setMaxHeight(312);
        chatGridPane.prefHeight(312);

        chatScrollPane.setMaxHeight(312);
        chatScrollPane.prefHeight(312);
    }

    private Label createReplyMessageLabel(String message) {
        Label messageLabel = new Label();
        messageLabel.setText(message);

        messageLabel.setStyle(
                """
                        -fx-background-color: rgba(255, 255, 255, .1);
                        -fx-border-radius: 20px;
                        -fx-background-radius: 20px;
                        -fx-font-size: 17px;
                        -fx-padding: 10px 10px 20px;
                        -fx-text-fill: white;
                        -fx-opacity: 0.4;
                        -fx-border-color: linear-gradient(to right top, #0c0537, #1c0c44, #2f0d51, #440c5c, #590766);
                        -fx-border-width: 0.6;
                        """.indent(4));

        return messageLabel;
    }

    private Label createMessageLabel(String message, User sender) {
        Label messageLabel = new Label();
        messageLabel.setText(message);

        if (controller.verifyIfUserIsCurrentUser(sender)) {
            messageLabel.setStyle(
                    """
                            -fx-background-color: \"0037A5\";
                            -fx-border-radius: 20px;
                            -fx-background-radius: 20px;
                            -fx-font-size: 17px;
                            -fx-padding: 10px;
                            -fx-text-fill: white;
                            -fx-border-color: linear-gradient(to right top, #0c0537, #1c0c44, #2f0d51, #440c5c, #590766);
                            -fx-border-width: 0.6;
                            """.indent(4));
        } else {
            messageLabel.setStyle(
                    """
                            -fx-background-color: #404040;
                            -fx-border-radius: 20px;
                            -fx-background-radius: 20px;
                            -fx-font-size: 17px;
                            -fx-padding: 10px;
                            -fx-text-fill: white;
                            -fx-border-color: linear-gradient(to right top, #0c0537, #1c0c44, #2f0d51, #440c5c, #590766);
                            -fx-border-width: 0.6;
                            """.indent(4));
        }

        return messageLabel;
    }

    // sa schimb sa se trimita mesaju in functie de username nu de nume
    public void sendMessage() {
        if (replyMode) {
            replyToMessage();
        } else {
            String message = chatTextField.getText();
            List<String> to = List.of(user.getFirstName() + " " + user.getLastName());

            this.controller.sendMessageToUsersFromCurrentUser(message, to);

            List<Message> conversation = controller.currentUsersConversationWithAnotherUser(user.getUsername()).stream().filter(message1 -> controller.verifyIfUserIsCurrentUser(message1.getFrom())).collect(Collectors.toList());

            Label messageLabel = createMessageLabel(message, controller.getCurrentUser());
            placeMessageLabel(messageLabel, controller.getCurrentUser(), null, conversation.get(conversation.size() - 1));
        }
    }

    public void replyToMessage() {
        String message = chatTextField.getText();

        System.out.println(messageToReply);

        this.controller.currentUserReply(user.getFirstName(), user.getLastName(), message, messageToReply.getId());

        List<Message> conversation = controller.currentUsersConversationWithAnotherUser(user.getUsername()).stream().filter(message1 -> controller.verifyIfUserIsCurrentUser(message1.getFrom())).collect(Collectors.toList());

        Label messageLabel = createMessageLabel(message, controller.getCurrentUser());
        placeMessageLabel(messageLabel, controller.getCurrentUser(), messageToReply, conversation.get(conversation.size() - 1));
    }

    public void initConversation() {
        List<Message> conversation = controller.currentUsersConversationWithAnotherUser(user.getUsername());

        clearGridPane();
        this.row = 0;

        conversation.forEach(message -> {
            Label messageLabel = createMessageLabel(message.getMessage(), message.getFrom());

            placeMessageLabel(messageLabel, message.getFrom(), message.getReply(), message);
        });
    }

    @Override
    public void update() {
        initChatList();
    }
}
