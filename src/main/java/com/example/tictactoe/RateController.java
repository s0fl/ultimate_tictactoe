package com.example.tictactoe;

import Model.Table;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class RateController {
    public TableView<Table> table;
    public TableColumn<Table, Integer> id;
    public TableColumn<Table, String> name;
    public TableColumn<Table, Integer> win;
    public TableColumn<Table, Integer> lose;
    public TableColumn<Table, Integer> percent;
    private ObservableList<Table> list;

    public RateController(ObservableList<Table> observableList){
        this.list = observableList;
    }

    @FXML
    public void initialize(){
        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        win.setCellValueFactory(new PropertyValueFactory<>("win"));
        lose.setCellValueFactory(new PropertyValueFactory<>("lose"));
        percent.setCellValueFactory(new PropertyValueFactory<>("percent"));
        table.setItems(list);
    }
}
