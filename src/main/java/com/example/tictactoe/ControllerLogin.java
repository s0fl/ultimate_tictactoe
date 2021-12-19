package com.example.tictactoe;

import Client.Client;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

import java.util.ResourceBundle;

public class ControllerLogin implements Initializable {
    @FXML
    private Button button_login;
    @FXML
    private TextField login = null;
    @FXML
    private PasswordField pass = null;
    @FXML
    private TextField textField;
    @FXML
    private Button button_registration;
    private Client client;
    private Listener listener;
    private String name;
    private boolean check = false;
    private boolean flag_send = false;

    public ControllerLogin(Client client) {
        this.client = client;
        listener = new Listener();
        listener.start();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        button_login.setOnAction(actionEvent -> this.loginButtonAction());
        button_registration.setOnAction(actionEvent -> {
            try {
                this.click_registration();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void click_registration() throws IOException {
        listener.interrupt();
        ControllerRegistration registration = new ControllerRegistration(client);
        changeWindow("registration", button_registration, true, registration);
    }

    public void loginButtonAction() {
        if (!login.getText().isBlank() && !pass.getText().isBlank()) {
            client.sendDataToServer("@login " + login.getText() + " " + pass.getText());
            flag_send = true;
        } else {
            textField.setText("please enter username or password");
        }
    }

    public void checkLogin() throws IOException {
        flag_send = false;
        name = login.getText();
        textField.setText("welcome");
        client.setName(name);
        System.out.println("ContNAME: " + name);
        MenuController menuController = new MenuController(client);
        changeWindow("menu", button_login, true, menuController);
        if (!listener.isInterrupted())
            listener.interrupt();
    }

    public void setTextField() {
        textField.setText("are you sure? :)");
        flag_send = false;
    }

    public void changeWindow(String nameFXML, Button button, boolean close, Object controller) throws IOException {
        Stage stage;
        if (close) {
            stage = (Stage) button.getScene().getWindow();
            stage.close();
        }
        stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(nameFXML + ".fxml"));
        if (controller != null)
            fxmlLoader.setController(controller);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(nameFXML);
        stage.setResizable(false);
        stage.setScene(new Scene(fxmlLoader.load()));
        stage.show();
    }

    public class Listener extends Thread {
        @Override
        public void run() {
            while (!ControllerLogin.Listener.currentThread().isInterrupted()) {

                if (client.getCheckLogin()) {
                    Platform.runLater(() -> {
                        try {
                            checkLogin();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                    if (!this.isInterrupted()) {
                        this.interrupt();
                    }
                }
                if (flag_send && !client.getCheckLogin()) {
                    Platform.runLater(() -> setTextField());
                }
            }
        }
    }
}