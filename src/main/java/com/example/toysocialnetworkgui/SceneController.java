package com.example.toysocialnetworkgui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneController {
    private Stage primaryStage;
    private Scene loginScene;
    private Scene mainScene;

    private LoginViewController loginViewController;
    private MainViewController mainViewController;

    public SceneController(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    private void initLoginScene() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("Views/login-view2.fxml"));

        loginScene = new Scene(fxmlLoader.load(), 50, 450);
        loginViewController = fxmlLoader.getController();
    }

    private void initMainScene() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("Views/main-view.fxml"));

        mainScene = new Scene(fxmlLoader.load());
        mainViewController = fxmlLoader.getController();
    }

    public void changeToLoginScene() throws IOException {
        initLoginScene();
        this.primaryStage.setScene(loginScene);
    }

    public void changeToMainScene() throws IOException {
        initMainScene();
        this.primaryStage.setScene(mainScene);
        this.primaryStage.setWidth(1000);
        this.primaryStage.setHeight(600);
        this.primaryStage.setMaxWidth(1000);
    }

    public LoginViewController getLoginViewController() {
        return loginViewController;
    }

    public MainViewController getMainViewController() {
        return mainViewController;
    }
}
