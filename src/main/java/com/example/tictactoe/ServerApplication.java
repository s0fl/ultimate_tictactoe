package com.example.tictactoe;


import Server.Server;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ServerApplication extends Application {
    Server server;
    ControllerServer controllerServer;

    @Override
    public void start(Stage stage) throws Exception {
        server = new Server();
        controllerServer = new ControllerServer(server);
        FXMLLoader fxmlLoader = new FXMLLoader(ServerApplication.class.getResource("server.fxml"));
        fxmlLoader.setController(controllerServer);
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("server");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        server.stopServer();
    }

    public static void main(String[] args) {
        launch();
    }
}
