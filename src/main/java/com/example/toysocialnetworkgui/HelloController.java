package com.example.toysocialnetworkgui;

import com.example.toysocialnetworkgui.controller.Controller;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloController {

    @FXML
    Label loginLabel;
    @FXML
    Label registerLabel;
    @FXML
    VBox registerForm;
    @FXML
    VBox loginForm;

    @FXML
    private TextField first_name_textfield;
    @FXML
    private TextField last_name_textfield;
    Controller controler;

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
    protected void onLoginButtonClick()
    {
        try {
            String first_name = first_name_textfield.getText();
            String last_name = last_name_textfield.getText();
            //System.out.println(first_name+" "+ last_name);
            controler.changeCurrentUser(first_name, last_name);
            showViewMenu();
        } catch(Exception e) {
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION,"Eroare",e.getMessage());
        }
    }
    public void showViewMenu()  {
        try {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("Views/main_view.fxml"));
        VBox root = (VBox) loader.load();



        Stage dialogStage = new Stage();
        dialogStage.setTitle("");
        dialogStage.initModality(Modality.WINDOW_MODAL);

        Scene scene = new Scene(root, 600, 450);
        dialogStage.setScene(scene);
        MainViewController mainViewController=loader.getController();
        mainViewController.setController(controler, dialogStage);
        dialogStage.show();
        } catch (IOException e) {
        e.printStackTrace();
       }
    }
    public void setController(Controller ctrl)
    {
        controler=ctrl;
    }
}

