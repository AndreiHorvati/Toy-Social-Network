package com.example.toysocialnetworkgui;

import com.example.toysocialnetworkgui.controller.Controller;
import com.example.toysocialnetworkgui.model.validators.UserValidationException;
import com.example.toysocialnetworkgui.repository.ExistingUserException;
import com.example.toysocialnetworkgui.repository.database.DatabaseException;
import com.example.toysocialnetworkgui.service.NonExistingUserException;
import com.example.toysocialnetworkgui.service.WrongPasswordException;
import com.example.toysocialnetworkgui.utils.PasswordHasher;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class LoginViewController {
    Controller controller;
    SceneController sceneController;

    @FXML
    Label loginLabel;
    @FXML
    Label registerLabel;
    @FXML
    VBox registerForm;
    @FXML
    VBox loginForm;

    @FXML
    TextField usernameTextFieldRegister;
    @FXML
    TextField firstNameTextFieldRegister;
    @FXML
    TextField lastNameTextFieldRegister;
    @FXML
    TextField passwordTextFieldRegister;

    @FXML
    TextField usernameTextFieldLogin;
    @FXML
    TextField passwordTextFieldLogin;

    @FXML
    Label errorLabelRegister;
    @FXML
    Label errorLabelLogin;

    @FXML
    private void onLoginLabelClick() {
        loginLabel.setTextFill(Color.valueOf("white"));
        registerLabel.setTextFill(Color.valueOf("#9a9a9a"));
        registerForm.setVisible(false);
        loginForm.setVisible(true);

    }

    @FXML
    private void onRegisterLabelClick() {
        loginLabel.setTextFill(Color.valueOf("#9a9a9a"));
        registerLabel.setTextFill(Color.valueOf("white"));
        registerForm.setVisible(true);
        loginForm.setVisible(false);
    }

    @FXML
    private void onRegisterButtonClick() {
        String username = usernameTextFieldRegister.getText();
        String firstName = firstNameTextFieldRegister.getText();
        String lastName = lastNameTextFieldRegister.getText();
        String password = passwordTextFieldRegister.getText();

        try {
            PasswordHasher passwordHasher = new PasswordHasher();

            controller.saveUser(firstName, lastName, username, passwordHasher.getHashedPassword(password));
        } catch (IllegalArgumentException | UserValidationException | ExistingUserException | DatabaseException e) {
            errorLabelRegister.setText(e.getMessage());
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onLoginButtonClick() {
        String username = usernameTextFieldLogin.getText();
        String password = passwordTextFieldLogin.getText();

        try {
            controller.login(username, password);
            changeToMainWindow();
        } catch(NonExistingUserException | WrongPasswordException e) {
            errorLabelLogin.setText(e.getMessage());
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void setSceneController(SceneController sceneController) {
        this.sceneController = sceneController;
    }

    private void changeToMainWindow() {
        try {
            this.sceneController.changeToMainScene();

            MainViewController mainViewController = this.sceneController.getMainViewController();

            mainViewController.setController(controller);
            mainViewController.setSearchBarEntries();
            mainViewController.setSceneController(sceneController);
            mainViewController.loadProfile(controller.getUserPage(controller.getCurrentUser()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
