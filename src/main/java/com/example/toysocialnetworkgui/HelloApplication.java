package com.example.toysocialnetworkgui;

import com.example.toysocialnetworkgui.controller.Controller;
import com.example.toysocialnetworkgui.model.validators.FriendshipValidator;
import com.example.toysocialnetworkgui.model.validators.MessageValidator;
import com.example.toysocialnetworkgui.model.validators.UserValidator;
import com.example.toysocialnetworkgui.repository.database.FriendshipDatabaseRepository;
import com.example.toysocialnetworkgui.repository.database.MessageDatabaseRepository;
import com.example.toysocialnetworkgui.repository.database.UserDatabaseRepository;
import com.example.toysocialnetworkgui.service.AuthenticationService;
import com.example.toysocialnetworkgui.service.FriendshipService;
import com.example.toysocialnetworkgui.service.MessageService;
import com.example.toysocialnetworkgui.service.UserService;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    UserValidator userValidator;
    FriendshipValidator friendshipValidator;
    MessageValidator messageValidator;

    UserDatabaseRepository userRepository;
    FriendshipDatabaseRepository friendshipRepository;
    MessageDatabaseRepository messageRepository;

    UserService userService;
    FriendshipService friendshipService;
    MessageService messageService;
    AuthenticationService authenticationService;

    Controller controller;

    private void initializeController() {
        userValidator = new UserValidator();
        friendshipValidator = new FriendshipValidator();
        messageValidator = new MessageValidator();

        userRepository = new UserDatabaseRepository("jdbc:postgresql://localhost:5432/ToySocialNetwork",
                "postgres", "postgres", userValidator);
        friendshipRepository = new FriendshipDatabaseRepository("jdbc:postgresql://localhost:5432/ToySocialNetwork",
                "postgres", "postgres", friendshipValidator);
        messageRepository = new MessageDatabaseRepository("jdbc:postgresql://localhost:5432/ToySocialNetwork",
                "postgres", "postgres", messageValidator);

        userService = new UserService(userRepository);
        friendshipService = new FriendshipService(friendshipRepository);
        messageService = new MessageService(messageRepository);
        authenticationService = new AuthenticationService(userRepository);

        controller = new Controller(userService, friendshipService, messageService, authenticationService);
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        initView(primaryStage);
        primaryStage.setWidth(400);
        primaryStage.setHeight(580);

        primaryStage.show();
    }

    private void initView(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login-view2.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 50, 450);

        primaryStage.setTitle("Login");
        primaryStage.setScene(scene);
        primaryStage.show();

        initializeController();

        LoginViewController helloController = fxmlLoader.getController();
        helloController.setController(controller);
    }
}