package com.example.tictactoe;

import Client.Client;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AfterGameController implements Initializable {
    @FXML
    private Label label;
    @FXML
    private Button returnButton;
    private Client client;
    private String status;

    public AfterGameController(Client client, String status){
        this.client = client;
        this.status = status;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        label.setText(client.getName() + ", " + status);
    }

    @FXML
    private void returnToMenu(MouseEvent mouseEvent){
        Stage stage = (Stage) returnButton.getScene().getWindow();
        stage.close();
        MenuController Controller = new MenuController(client);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("menu.fxml"));
        fxmlLoader.setController(Controller);
        stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("menu");
        try {
            stage.setScene(new Scene(fxmlLoader.load()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.show();
    }
}
