package com.example.tictactoe;

import Server.Server;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import java.net.URL;
import java.util.ResourceBundle;

public class ControllerServer implements Initializable {
    @FXML
    private Button button;
    private Server server;

    public ControllerServer(Server server) {
        this.server = server;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        button.setOnAction(actionEvent -> this.startServer(actionEvent));
    }

    private void startServer(ActionEvent actionEvent) {
        Button button = (Button) actionEvent.getSource();
        button.setDisable(true);
        server.getStartServer().start();
    }
}
