package Server;

import IO.ConnectionDB;
import Logger.LogClass;
import Model.Rating;
import com.google.gson.Gson;
import java.io.*;
import java.net.Socket;

public class WaitingThread extends Thread {
    private Server server;
    private Socket socket;
    private BufferedWriter out;
    private BufferedReader in;
    private String nameUser;

    public WaitingThread(Socket socket, Server server) {
        try {
            this.nameUser = "null";
            this.socket = socket;
            this.server = server;
            this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
            LogClass.logError(WaitingThread.class, e.getMessage());
        }
    }

    public WaitingThread(Socket socket, Server server, String name) {
        try {
            this.nameUser = name;
            this.socket = socket;
            this.server = server;
            this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.start();
        } catch (IOException e) {
            e.printStackTrace();
            LogClass.logError(WaitingThread.class, e.getMessage());
        }
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                String str = in.readLine();
                if (str.contains("@login")) {
                    String[] data = str.split(" ");
                    String login = data[1];
                    String pass = data[2];
                    LogClass.logCommunicate("login " + login + " " + pass);
                    ConnectionDB connectionDB = new ConnectionDB();
                    if (connectionDB.checkLogin(login, pass)) {
                        sendToClient("@auth ok");
                        nameUser = login;
                        server.serverList.addLast(this);
                    }
                }
                if (str.contains("@registration")) {
                    String[] data = str.split(" ");
                    String login = data[1];
                    String pass = data[2];
                    LogClass.logCommunicate("@registration " + login + " " + pass);
                    ConnectionDB connectionDB = new ConnectionDB();
                    if (connectionDB.checkRegistration(login, pass)) {
                        sendToClient("@registration ok");
                    }
                }
                if (str.contains("@name")) {
                    nameUser = str.substring(6);
                    server.serverList.addLast(this);
                    LogClass.logCommunicate("Name: " + nameUser);
                }
                if (str.contains("@load_rate")) {
                    ConnectionDB connectionDB = new ConnectionDB();
                    Rating rating = connectionDB.loadRate();
                    Gson gson = new Gson();
                    String gs = gson.toJson(rating);
                    sendToClient("@rate " + gs);
                }
                if (str.contains("@upgrade_rate")) {
                    ConnectionDB connectionDB = new ConnectionDB();
                    connectionDB.loadRate();
                }
                if(str.contains("@new_game")) {
                    server.gameList.addLast(new GameThread(socket, server, nameUser));
                    LogClass.logCommunicate("the first player tries to get into the game");
                    sendToClient("@player X");
                    sendToClient("@new_game");
                    server.serverList.remove(this);
                    this.interrupt();
                }
                if (str.contains("@game")) {
                    boolean isAdd = false;
                    for (GameThread game : server.gameList) {
                        System.out.println("GameList: " + server.gameList);
                        if (!game.isGame() && !isAdd) {
                            LogClass.logCommunicate("the second player tries to get into the game");
                            sendToClient("@player O");
                            game.add(socket, nameUser);
                            isAdd = true;
                            server.serverList.remove(this);
                            this.interrupt();
                        }
                    }
                    if (!isAdd) {
                        server.gameList.addLast(new GameThread(socket, server, nameUser));
                        LogClass.logCommunicate("the first player tries to get into the game");
                        sendToClient("@player X");
                        sendToClient("@new_game");
                        server.serverList.remove(this);
                        this.interrupt();
                    }
                }
                if (str.equals("@close")) {
                    sendToClient(str);
                    LogClass.logCommunicate("bye" + nameUser);
                    server.serverList.remove(this);
                    this.interrupt();
                }
                else
                    LogClass.logCommunicate("WaitingThread: " + nameUser + " " + str);
            } catch (IOException e) {
                e.printStackTrace();
                LogClass.logError(WaitingThread.class, e.getMessage());
            }

        }
    }

    public void sendToClient(String str) throws IOException {
        out.write(str);
        out.newLine();
        out.flush();
    }
}
