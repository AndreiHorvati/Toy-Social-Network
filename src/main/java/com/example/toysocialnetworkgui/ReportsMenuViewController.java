package com.example.toysocialnetworkgui;

import com.example.toysocialnetworkgui.controller.Controller;
import com.example.toysocialnetworkgui.model.Message;
import com.example.toysocialnetworkgui.model.ReportItem;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ReportsMenuViewController {
    Controller controller;

    Stage stage;

    @FXML
    DatePicker datePicker1;
    @FXML
    DatePicker datePicker2;
    @FXML
    VBox vBox;
    @FXML
    DatePicker datePicker11;
    @FXML
    DatePicker datePicker21;
    @FXML
    TextField friendTextField;

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void generateReport2() {
        LocalDateTime date1 = LocalDateTime.of(datePicker11.getValue(), LocalTime.MIDNIGHT);
        LocalDateTime date2 = LocalDateTime.of(datePicker21.getValue(), LocalTime.MIDNIGHT);
        String username = friendTextField.getText();

        List<Message> messages = controller
                .getAllMessagesReceivedByCurrentUserFromAnotherUserBetweenTwoDates(username, date1, date2);

        vBox.getChildren().clear();

        messages.forEach(message -> {
            Label label = new Label();

            label.setText("Ati primit un mesajul " + message.getMessage() + " de la " + message.getFrom().getFirstName() + " " +
                    message.getFrom().getLastName() + " la data " + message.getDate().getDayOfMonth() +
                    " " + message.getDate().getMonth().getValue() + " " +
                    message.getDate().getYear() + "!");
            label.setStyle(
                    """
                            -fx-background-color: \"0037A5\";
                            -fx-border-radius: 20px;
                            -fx-background-radius: 20px;
                            -fx-font-size: 10px;
                            -fx-padding: 5px;
                            -fx-text-fill: white;
                            -fx-border-color: linear-gradient(to right top, #0c0537, #1c0c44, #2f0d51, #440c5c, #590766);
                            -fx-border-width: 0.6;
                            """.indent(4));

            vBox.getChildren().add(label);
        });
    }

    public void generateReport1() {
        LocalDateTime date1 = LocalDateTime.of(datePicker1.getValue(), LocalTime.MIDNIGHT);
        LocalDateTime date2 = LocalDateTime.of(datePicker2.getValue(), LocalTime.MIDNIGHT);

        List<ReportItem> reportItemList = controller
                .getAllFriendshipsAndReceivedMessagesByCurrentUserBetweenTwoDates(date1, date2);

        vBox.getChildren().clear();

        reportItemList.forEach(reportItem -> {
            Label label = new Label();

            label.setText(reportItem.getContent());
            label.setStyle(
                    """
                            -fx-background-color: \"0037A5\";
                            -fx-border-radius: 20px;
                            -fx-background-radius: 20px;
                            -fx-font-size: 10px;
                            -fx-padding: 5px;
                            -fx-text-fill: white;
                            -fx-border-color: linear-gradient(to right top, #0c0537, #1c0c44, #2f0d51, #440c5c, #590766);
                            -fx-border-width: 0.6;
                            """.indent(4));

            vBox.getChildren().add(label);
        });
    }

    public void generatePDF() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(stage);

        List<String> content = vBox.getChildren().stream().map(node -> {
            Label label = (Label) node;

            return label.getText();
        }).collect(Collectors.toList());

        PDFWriter pdfWriter = new PDFWriter(selectedDirectory.getPath());
        pdfWriter.writeToFile(content);
    }
}
