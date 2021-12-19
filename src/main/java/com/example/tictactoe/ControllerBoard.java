package com.example.tictactoe;

import Client.Client;
import Model.SendDataBoard;
import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ResourceBundle;


public class ControllerBoard implements Initializable {
    @FXML
    private GridPane block_0;
    @FXML
    private GridPane block_1;
    @FXML
    private GridPane block_2;
    @FXML
    private GridPane block_3;
    @FXML
    private GridPane block_4;
    @FXML
    private GridPane block_5;
    @FXML
    private GridPane block_6;
    @FXML
    private GridPane block_7;
    @FXML
    private GridPane block_8;
    @FXML
    private Label statusBar;
    @FXML
    private Label loginLabel;
    @FXML
    private Button returnButton;

    private ArrayList<GridPane> blocks = new ArrayList<>();
    private OpponentMove oppMover;
    private LinkedList<Integer> winnersList;
    private Client client;

    public ControllerBoard(Client client) {
        winnersList = new LinkedList<>();
        this.client = client;
        oppMover = new OpponentMove();
        oppMover.start();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        returnButton.setOnMouseClicked(mouseEvent -> exit(mouseEvent));
        blocks.add(block_0);
        blocks.add(block_1);
        blocks.add(block_2);
        blocks.add(block_3);
        blocks.add(block_4);
        blocks.add(block_5);
        blocks.add(block_6);
        blocks.add(block_7);
        blocks.add(block_8);
        if (this.client.getPlayer().equals("O")) {
            returnButton.setDisable(true);
        }
        blockAll();
        statusBar.setText("Player " + client.getPlayer());
        loginLabel.setText("Login: " + client.getName() + ". Player "+ client.getPlayer());
    }

    @FXML
    public void onClickChange(MouseEvent mouseEvent) {
        String bStyle = String.format("-fx-background-color: f7adadd4;");

        Button button = (Button) mouseEvent.getSource();
        button.setStyle(bStyle);
        client.sendDataToServer("@move " + button.getId());

        unblock();
        button.setText(client.getPlayer());
        button.setStyle("-fx-background-color: rgba(51,232,23,0.8)");
        button.setDisable(true);
        blockAll();
        returnButton.setDisable(true);
        statusBar.setText("Your opponent's move");
    }

    public void setEnd(String data) {
        Gson gson = new Gson();
        SendDataBoard dataBoard = gson.fromJson(data, SendDataBoard.class);

        if (dataBoard.isSmallWin()) {
            blocks.get(dataBoard.getBlock()).setDisable(true);
            String Style = "-fx-background-color: rgba(0,128,0,0.9)";
            blocks.get(dataBoard.getBlock()).setStyle(Style);
            winnersList.addLast(dataBoard.getBlock());
        }

        if (dataBoard.isBigWin()) {
            blockAll();
            this.exit("You are win!");
        }
    }


    @FXML
    private synchronized void setMove(String data) {
        Gson gson = new Gson();
        SendDataBoard dataBoard = gson.fromJson(data, SendDataBoard.class);
        Button button = getButton(dataBoard.getButtonId());
        System.out.println(button.getId());
        System.out.println("Opp move");

        String bStyle = String.format("-fx-background-color: f7adadd4;");
        button.setStyle(bStyle);
        statusBar.setText(button.getId());

        unblock();

        if (client.getPlayer().equals("X")) {
            button.setText("O");
        } else {
            button.setText("X");
        }
        button.setStyle("-fx-background-color: rgba(211,12,12,0.85)");
        button.setDisable(true);

        if (dataBoard.isSmallWin()) {
            blocks.get(dataBoard.getBlock()).setDisable(true);
            String Style = "-fx-background-color: rgba(139,17,17,0.95)";
            blocks.get(dataBoard.getBlock()).setStyle(Style);
            winnersList.addLast(dataBoard.getBlock());
        }

        if (dataBoard.isBigWin()) {
            client.sendDataToServer("@quit "+ client.getName() + " lose");
            this.exit("You are lose :(");
        } else {
            int nextSmallField = dataBoard.getNextBlock();
            if (nextSmallField > -1)
                if (!winnersList.contains(nextSmallField)) {
                    block(nextSmallField);
                } else {
                    unblock();
                }
            returnButton.setDisable(false);
        }
        statusBar.setText("Your move");
    }

    private void unblock() {
        int count = 0;
        for (GridPane block : blocks) {
            if(!winnersList.contains(count)) {
                block.setDisable(false);
            }
            count++;
        }
    }

    private void block(int numberBlock) {
        for (int i = 0; i < blocks.size(); i++) {
            if (i != numberBlock) {
                blocks.get(i).setDisable(true);
            } else {
                blocks.get(i).setDisable(false);

            }
        }
    }

    private void blockAll(){
        for (int i = 0; i < blocks.size(); i++) {
            blocks.get(i).setDisable(true);
        }
    }

    public Button getButton(String buttonId){
        String[]id = buttonId.split("_");
        ObservableList<Node> list = blocks.get(Integer.parseInt(id[1])).getChildren();
        for(Node btn : list){
            if(btn.getId().equals(buttonId))
                return (Button) btn;
        }
        return null;
    }

    private void exit(String status){
        this.oppMover.interrupt();
        client.setPlayer("none");
        client.clearMove();
        System.out.println(status);
        Stage stage = (Stage) returnButton.getScene().getWindow();
        stage.close();
        AfterGameController Controller = new AfterGameController(client, status);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("afterGame.fxml"));
        fxmlLoader.setController(Controller);
        stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("message");
        stage.setResizable(false);
        try {
            stage.setScene(new Scene(fxmlLoader.load()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.show();
    }

    private void exit(MouseEvent mouseEvent){
        this.oppMover.interrupt();
        client.sendDataToServer("@quit " + client.getName() + " lose");
        client.setPlayer("none");
        String status;
        if(client.getCheckLogin())
            status = "You left the game";
        else
            status = "You automatically lose";
        client.clearMove();
        System.out.println(status);
        Button button = (Button) mouseEvent.getSource();
        Stage stage = (Stage) button.getScene().getWindow();
        stage.close();
        AfterGameController Controller = new AfterGameController(client, status);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("afterGame.fxml"));
        fxmlLoader.setController(Controller);
        stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("message");
        try {
            stage.setScene(new Scene(fxmlLoader.load()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.show();
    }

    public class OpponentMove extends Thread {
        @Override
        public void run() {
            boolean check = false;
            while (!this.isInterrupted()) {
                if (!check && client.getPlayer().contains("X") && !client.getCheckLogin()) {
                    Platform.runLater(() -> unblock());
                    check = true;
                    System.out.println("Checked");
                }
                else if (client.getPlayer().contains("O"))
                    check = true;
                String c = client.getMove();
                if (c != null) {
                    if (c.contains("@O")) {
                        if (client.getPlayer().equals("X")) {
                            Platform.runLater(() -> setMove(c.substring(3)));
                        } else {
                            Platform.runLater(() -> setEnd(c.substring(3)));
                        }
                    } else if (c.contains("@X")) {
                        if (client.getPlayer().equals("O")) {
                            Platform.runLater(() -> setMove(c.substring(3)));
                        } else {
                            Platform.runLater(() -> setEnd(c.substring(3)));
                        }
                    } else if (c.contains("@quit") && check) {
                        String status = "You automatically win";
                        System.out.println(status);
                        Platform.runLater(() -> {
                            exit(status);
                        });

                            if (!this.isInterrupted())
                                this.interrupt();
                            break;
                        }
                }
            }
        }
    }
}
