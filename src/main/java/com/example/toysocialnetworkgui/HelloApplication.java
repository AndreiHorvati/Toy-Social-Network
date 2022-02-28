package com.example.toysocialnetworkgui;

import com.example.toysocialnetworkgui.controller.Controller;
import com.example.toysocialnetworkgui.model.Event;
import com.example.toysocialnetworkgui.model.validators.EventValidator;
import com.example.toysocialnetworkgui.model.validators.FriendshipValidator;
import com.example.toysocialnetworkgui.model.validators.MessageValidator;
import com.example.toysocialnetworkgui.model.validators.UserValidator;
import com.example.toysocialnetworkgui.repository.database.EventDatabaseRepository;
import com.example.toysocialnetworkgui.repository.database.FriendshipDatabaseRepository;
import com.example.toysocialnetworkgui.repository.database.MessageDatabaseRepository;
import com.example.toysocialnetworkgui.repository.database.UserDatabaseRepository;
import com.example.toysocialnetworkgui.service.*;
import javafx.application.Application;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;

public class HelloApplication extends Application {
    UserValidator userValidator;
    FriendshipValidator friendshipValidator;
    MessageValidator messageValidator;
    EventValidator eventValidator;

    UserDatabaseRepository userRepository;
    FriendshipDatabaseRepository friendshipRepository;
    MessageDatabaseRepository messageRepository;
    EventDatabaseRepository eventRepository;

    UserService userService;
    FriendshipService friendshipService;
    MessageService messageService;
    AuthenticationService authenticationService;
    EventService eventService;

    Controller controller;
    NotificationsThread notificationsThread;

    private void initializeController() {
        userValidator = new UserValidator();
        friendshipValidator = new FriendshipValidator();
        messageValidator = new MessageValidator();
        eventValidator = new EventValidator();

        userRepository = new UserDatabaseRepository("jdbc:postgresql://localhost:5432/ToySocialNetwork",
                "postgres", "postgres", userValidator);
        friendshipRepository = new FriendshipDatabaseRepository("jdbc:postgresql://localhost:5432/ToySocialNetwork",
                "postgres", "postgres", friendshipValidator);
        messageRepository = new MessageDatabaseRepository("jdbc:postgresql://localhost:5432/ToySocialNetwork",
                "postgres", "postgres", messageValidator);
        eventRepository = new EventDatabaseRepository("jdbc:postgresql://localhost:5432/ToySocialNetwork",
                "postgres", "postgres", eventValidator);

        userService = new UserService(userRepository);
        friendshipService = new FriendshipService(friendshipRepository);
        messageService = new MessageService(messageRepository);
        authenticationService = new AuthenticationService(userRepository);
        eventService = new EventService(eventRepository);

        controller = new Controller(userService, friendshipService, messageService, authenticationService, eventService);

        notificationsThread = new NotificationsThread(controller);
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
        primaryStage.centerOnScreen();

        primaryStage.setOnCloseRequest(e -> {
            notificationsThread.stop();
            Platform.exit();
            System.exit(0);
        });
    }

    private void initView(Stage primaryStage) throws IOException {
        SceneController sceneController = new SceneController(primaryStage);

        primaryStage.setTitle("ToySocialNetwork");
        sceneController.changeToLoginScene();
        primaryStage.show();

        initializeController();

        LoginViewController loginViewController = sceneController.getLoginViewController();

        loginViewController.setNotificationThread(notificationsThread);
        loginViewController.setController(controller);
        loginViewController.setSceneController(sceneController);
    }
}