package com.example.toysocialnetworkgui;

/*
import com.example.toysocialnetworkgui.controller.Controller;
import com.example.toysocialnetworkgui.model.FriendRequestDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class FriendRequestsController {
    ObservableList<FriendRequestDTO> model = FXCollections.observableArrayList();

    @FXML
    TableView<FriendRequestDTO> tableView;
    @FXML
    TableColumn<FriendRequestDTO,String> tableColumnFirstName;
    @FXML
    TableColumn<FriendRequestDTO,String> tableColumnLastName;
    @FXML
    TableColumn<FriendRequestDTO, LocalDateTime> tableColumnDate;
    @FXML
    TableColumn<FriendRequestDTO,String> tableColumnStatus;

    private Controller controller;


    public void setController(Controller ctrl)
    {
        this.controller=ctrl;
        initModel();
    }

    private void initModel() {
        Iterable<FriendRequestDTO> friends = controller.getAllFriendRequestsFromCurrentUser();
        List<FriendRequestDTO> friendRequestsDTOList = StreamSupport.stream(friends.spliterator(), false)
                .collect(Collectors.toList());
        model.setAll(friendRequestsDTOList);

    }

    @FXML
    public void initialize() {
        // TODO
        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<FriendRequestDTO, String>("friendFirstName"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory<FriendRequestDTO, String>("friendLastName"));
        tableColumnDate.setCellValueFactory(new PropertyValueFactory<FriendRequestDTO, LocalDateTime>("date"));
        tableColumnStatus.setCellValueFactory(new PropertyValueFactory<FriendRequestDTO, String>("status"));
        tableView.setItems(model);
    }

    public void acceptFriendRequest() {
        FriendRequestDTO friendshipDTO = tableView.getSelectionModel().getSelectedItem();

        try {
            controller.approveFriendRequestFromCurrentUser(friendshipDTO.getFriendFirstName(), friendshipDTO.getFriendLastName());
            initModel();
        } catch(Exception e) {
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION,"Eroare", e.getMessage());
        }
    }

    public void declineFriendRequest() {
        FriendRequestDTO friendshipDTO = tableView.getSelectionModel().getSelectedItem();

        try {
            controller.rejectFriendRequestFromCurrentUser(friendshipDTO.getFriendFirstName(), friendshipDTO.getFriendLastName());
            initModel();
        } catch(Exception e) {
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION,"Eroare", e.getMessage());
        }
    }
}
*/