package com.example.tictactoe;

import Client.Client;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    ControllerLogin controllerLogin;
    Client client;

    @Override
    public void start(Stage stage) throws IOException {
        client = new Client();
        controllerLogin = new ControllerLogin(client);
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login.fxml"));
        fxmlLoader.setController(controllerLogin);
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("TicTacToe");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        client.close();
    }

    public static void main(String[] args) throws IOException {
        launch();
    }
}