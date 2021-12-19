package Client;

import java.io.*;
import java.net.Socket;

public class Client {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String name;
    private String player;
    private String move;
    private boolean checkLog;
    private boolean isMove;
    private boolean checkRegistration;

    public Client() {
        try {
            this.socket = new Socket("192.168.0.6", 2122);
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            new Read().start();
            move = null;
            checkLog = false;
            checkRegistration = false;
            isMove = false;
            player = "none";
        } catch (IOException e) {
            e.printStackTrace();
            close();
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void clearMove() {
        move = null;
        isMove = false;
        checkLog = true;
    }


    public String getPlayer() {
        return player;
    }

    public void setPlayer(String p) {
        player = p;
    }

    public synchronized String getMove() {
        if (!isMove && move != null) {
            System.out.println(move);
            isMove = true;
            return move;
        }
        return null;
    }

    public synchronized boolean getCheckLogin() {
        return checkLog;
    }

    public void sendDataToServer(String json) {
        try {
            bufferedWriter.write(json);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
            close();
        }
    }

    public void close() {
        try {
            sendDataToServer("@close");
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
            Read.currentThread().interrupt();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized boolean getCheckRegistration() {
        return checkRegistration;
    }

    public void setCheckRegistration(boolean b) {
        checkRegistration = b;
    }

    private class Read extends Thread {
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    String str = bufferedReader.readLine();
                    if (str != null) {
                        if (str.contains("@player")) {
                            player = str.substring(8);
                            System.out.println(name + " пришло с сервера: " + player);
                        } else if (str.contains("@move")) {
                            move = str.substring(6);
                            isMove = false;
                        } else if (str.contains("@X") || str.contains("@O")) {
                            move = str;
                            isMove = false;
                            System.out.println(move);
                        } else if (str.contains("@quit")) {
                            move = str;
                            isMove = false;
                            checkLog = true;
                        } else if (str.contains("@auth")) {
                            checkLog = true;
                            System.out.println("checkClient " + getCheckLogin());
                        } else if (str.contains("@registration")) {
                            checkRegistration = true;
                            System.out.println("checkClient " + getCheckLogin());
                        } else if (str.contains("@close")) {
                            System.out.println("client close");
                            this.interrupt();
                        } else if (str.contains("@rate")) {
                            move = str;
                            isMove = false;
                        } else if (str.contains("@new_game")) {
                            move = null;
                            isMove = true;
                            checkLog = true;
                        } else if (str.contains("@game")) {
                            move = null;
                            isMove = true;
                            checkLog = false;
                            if (player.contains("X")) {
                                sendDataToServer("@game");
                            }
                        }
                        System.out.println(name + " пришло с сервера: " + str);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
