package com.example.tictactoe;

import Client.Client;
import Model.Rating;
import Model.Table;
import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

public class MenuController {
    TableView<Table> table = new TableView<Table>();
    TableColumn<Table, Integer> id = new TableColumn<>();
    TableColumn<Table, String> name = new TableColumn<>();
    TableColumn<Table, Integer> win = new TableColumn<>();
    TableColumn<Table, Integer> lose = new TableColumn<>();
    TableColumn<Table, Integer> percent = new TableColumn<>();
    @FXML
    private Label label;
    @FXML
    private Button button_new_game;
    @FXML
    private Button button_join;
    @FXML
    private Button button_rules;
    @FXML
    private Button button_rate;
    private Client client;
    private Listener listener;

    public MenuController(Client client) {
        this.client = client;
        this.listener = new Listener();
        this.listener.start();
    }

    @FXML
    public void initialize() {
        button_new_game.setOnMouseClicked(mouseEvent -> {
            try {
                game(mouseEvent, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        button_join.setOnMouseClicked(mouseEvent -> {
            try {
                game(mouseEvent, false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        button_rate.setOnMouseClicked(mouseEvent -> {
            client.sendDataToServer("@load_rate");
        });
        button_rules.setOnMouseClicked(mouseEvent -> {
            try {
                openRules(mouseEvent);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        label.setText(client.getName());

        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        win.setCellValueFactory(new PropertyValueFactory<>("win"));
        lose.setCellValueFactory(new PropertyValueFactory<>("lose"));
        percent.setCellValueFactory(new PropertyValueFactory<>("percent"));
    }

    public void game(MouseEvent mouseEvent, boolean isNew) throws IOException {
        if (isNew)
            client.sendDataToServer("@new_game");
        else
            client.sendDataToServer("@game");
        if (!this.listener.isInterrupted())
            this.listener.interrupt();
        Button button = (Button) mouseEvent.getSource();
        changeWindow("tictactoe", button, true, new ControllerBoard(client));
    }

    public void openRules(MouseEvent mouseEvent) throws IOException {
        client.sendDataToServer("@rules");
        Button button = (Button) mouseEvent.getSource();
        changeWindow("rules", button, false, null);
    }

    public void setRate(String rate) {
        Gson gson = new Gson();
        Rating rating = gson.fromJson(rate, Rating.class);

        System.out.println("RATE");

        ObservableList<Table> list = FXCollections.observableArrayList();

        list.addAll(rating.getTables());

        System.out.println(list.get(0).getName() + list.get(0).getPercent());

        System.out.println("LIST " + list.size());
        for (Table value : list) {
            System.out.println(value.getName());
        }

        table.setItems(list);
        System.out.println(table.getItems());


        RateController rateController = new RateController(list);
        try {
            changeWindow("rate", button_rate, false, rateController);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            while (!MenuController.Listener.currentThread().isInterrupted()) {
                String c = client.getMove();
                if (c != null) {
                    if (c.contains("@quit")) {

                    } else {
                        if (c.contains("@rate")) {
                            Platform.runLater(() -> setRate(c.substring(6)));
                        }
                    }
                }
            }
        }
    }
}
