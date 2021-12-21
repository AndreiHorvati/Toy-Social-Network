package com.example.toysocialnetworkgui;

import com.example.toysocialnetworkgui.controller.Controller;
import com.example.toysocialnetworkgui.model.FriendshipDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class MainViewController implements Observer{
    ObservableList<FriendshipDTO> model = FXCollections.observableArrayList();

    @FXML
    TableView<FriendshipDTO> tableView;
    @FXML
    TableColumn<FriendshipDTO,String> tableColumnFirstName;
    @FXML
    TableColumn<FriendshipDTO,String> tableColumnLastName;
    @FXML
    TableColumn<FriendshipDTO, LocalDateTime> tableColumnDate;


    private Controller controller;
    private Stage stage;
    @FXML
    private TextField Add_friend_textfield;

    public void setController(Controller ctrl,Stage stg)
    {
        this.controller=ctrl;
        this.controller.addObserver(this);
        this.stage=stg;
        initModel();
    }

    private void initModel() {
        Iterable<FriendshipDTO> friends = controller.getAllFriendshipsFromCurrentUser();
        List<FriendshipDTO> friendshipDTOList = StreamSupport.stream(friends.spliterator(), false)
                .collect(Collectors.toList());
        model.setAll(friendshipDTOList);

    }

    @FXML
    public void initialize() {
        // TODO
        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<FriendshipDTO, String>("friendFirstName"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory<FriendshipDTO, String>("friendLastName"));
        tableColumnDate.setCellValueFactory(new PropertyValueFactory<FriendshipDTO, LocalDateTime>("date"));
        tableView.setItems(model);
    }

    public void addFriendship()
    {
        try {
            String name = Add_friend_textfield.getText();

            String[] names = name.split(" ");
            //System.out.println(names[0]+" "+ names[1]);
            controller.sendFriendRequestFromCurrentUser(names[0], names[1]);
        } catch(IndexOutOfBoundsException e) {
            com.example.toysocialnetworkgui.MessageAlert.showMessage(null, Alert.AlertType.INFORMATION,"Eroare","Numele unui utilizator trebuie sa fie format din 2 cuvinte!");
        } catch (Exception e) {
            com.example.toysocialnetworkgui.MessageAlert.showMessage(null, Alert.AlertType.INFORMATION,"Eroare", e.getMessage());
        }
    }


    public void removeFriend() {
        try {
            FriendshipDTO friendshipDTO = tableView.getSelectionModel().getSelectedItem();

            controller.deleteFriendFromCurrentUser(friendshipDTO.getFriendFirstName(), friendshipDTO.getFriendLastName());
            initModel();
        } catch(NullPointerException e) {
            com.example.toysocialnetworkgui.MessageAlert.showMessage(null, Alert.AlertType.INFORMATION,"Eroare", "Nu ati selectat niciun prieten!");
        }
    }

    public void showFriendRequest()
    {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("Views/FriendRequest-view.fxml"));
            VBox root = (VBox) loader.load();



            Stage dialogStage = new Stage();
            dialogStage.setTitle("");
            dialogStage.initModality(Modality.WINDOW_MODAL);

            Scene scene = new Scene(root, 600, 450);
            dialogStage.setScene(scene);
            com.example.toysocialnetworkgui.FriendRequestsController friendRequestsController=loader.getController();
            friendRequestsController.setController(controller);
            dialogStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update() {
        initModel();
    }
}
