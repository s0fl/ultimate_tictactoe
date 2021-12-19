package com.example.tictactoe;

import Client.Client;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ControllerRegistration implements Initializable {
    @FXML
    private TextField text_login;
    @FXML
    private TextField text_password;
    @FXML
    private Button button_create;
    @FXML
    private Button button_return;
    @FXML
    private TextField textField;
    @FXML
    private Client client;
    private Listener listener;
    private boolean flag_send = false;

    ControllerRegistration(Client client) {
        this.client = client;
        listener = new Listener();
        listener.start();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        button_create.setOnAction(actionEvent -> this.click_registration());
        button_return.setOnAction(actionEvent -> {
            try {
                this.click_return();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void click_return() throws IOException {
        listener.interrupt();
        ControllerLogin controllerLogin = new ControllerLogin(client);
        changeWindow("login", button_return, true, controllerLogin);
        if (!listener.isInterrupted())
            listener.interrupt();
    }

    public void click_registration() {
        if (!text_login.getText().isBlank() && !text_password.getText().isBlank()) {
            client.sendDataToServer("@registration " + text_login.getText() + " " + text_password.getText());
            flag_send = true;
        } else {
            textField.setText("please enter username and password");
        }
    }

    public void check_registration() throws IOException {
        flag_send = false;
        String name = text_login.getText();
        client.setName(name);
        client.setCheckRegistration(false);
        ControllerLogin controllerLogin = new ControllerLogin(client);
        changeWindow("login", button_create, true, controllerLogin);
        if (!listener.isInterrupted())
            listener.interrupt();
    }

    public void setTextField() {
        textField.setText("please, try again");
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
            while (!ControllerRegistration.Listener.currentThread().isInterrupted()) {
                if (client.getCheckRegistration()) {
                    Platform.runLater(() -> {
                        try {
                            check_registration();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                    if (!this.isInterrupted())
                        this.interrupt();
                }
                if (flag_send) {
                    if (!client.getCheckRegistration()) {
                        Platform.runLater(() -> setTextField());
                    }
                }

            }
        }
    }
}

