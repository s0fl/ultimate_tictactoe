package Server;

import IO.ConnectionDB;
import Logger.LogClass;
import Model.Board;
import Model.SendDataBoard;
import com.google.gson.Gson;

import java.io.*;
import java.net.Socket;

public class GameThread extends Thread {
    private Server server;
    private Socket socket1;
    private Socket socket2;
    private String nameUser1;
    private String nameUser2;
    private ConnectionDB dB;
    private BufferedWriter out;
    private BufferedReader in;
    private BufferedWriter out2;
    private BufferedReader in2;
    private Board board;
    private boolean isGame;
    private boolean player1;
    private SendDataBoard sendDataBoard;

    public GameThread(Socket socket, Server server, String name) throws IOException {
        this.nameUser1 = name;
        this.server = server;
        this.socket1 = socket;
        this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.board = new Board();
        isGame = false;
        player1 = true;
        LogClass.logCommunicate("Waiting the partner");
        this.start();
    }

    public void add(Socket socket, String name) throws IOException {
        this.nameUser2 = name;
        this.socket2 = socket;
        this.out2 = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.in2 = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        sendToClient("@game", out);
        sendToClient("@game", out2);
        isGame = true;
        dB = new ConnectionDB();
        board = new Board();
        sendDataBoard = new SendDataBoard();
        LogClass.logCommunicate( "GameThread: " + nameUser1 + " vs " + nameUser2);
    }

    public boolean isGame(){
        return isGame;
    }

    @Override
    public void run() {
        String move1 = "null";
        String move2;
        while (!Thread.currentThread().isInterrupted()) {
            if (!isGame) {
                try {
                    move1 = in.readLine();
                    LogClass.logCommunicate("GameThread Checking: " + nameUser1 + ": " + move1);
                } catch (IOException e) {
                    e.printStackTrace();
                    LogClass.logError(GameThread.class, e.getMessage());
                }
                if (move1.contains("@game")) {
                    continue;
                }
                if (move1.contains("@quit")) {
                    WaitingThread thread = new WaitingThread(socket1, server, nameUser1);
                    server.serverList.addLast(thread);
                    server.gameList.remove(this);
                    this.interrupt();
                    break;
                }
            } else {
                try {
                    if (player1) {
                        if (!move1.contains("@move"))
                            move1 = in.readLine();
                        LogClass.logCommunicate("GameThread: " + nameUser1 + ": " + move1);
                        if (move1.contains("@quit")) {
                            parse(move1);
                            sendToClient(move1, out2);
                            endGame();
                        } else {
                            String data = getData(move1);
                            sendToClient("@X " + data, out2);
                            sendToClient("@X " + data, out);
                            player1 = !player1;
                            move1 = "null";
                        }
                    } else {
                        move2 = in2.readLine();
                        LogClass.logCommunicate("GameThread: " + nameUser2 + ": " + move2);
                        if (move2.contains("@quit")) {
                            parse(move2);
                            sendToClient(move2, out);
                            endGame();
                            break;
                        } else {
                            String data = getData(move2);
                            sendToClient("@O " + data, out2);
                            sendToClient("@O " + data, out);
                            player1 = !player1;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    LogClass.logError(GameThread.class, e.getMessage());
                }
            }
        }
    }

    private String getData(String move) {
        int block = board.game(move.substring(6));
        sendDataBoard.setNextBlock(board.getNextSmallField());
        sendDataBoard.setBlock(block);
        sendDataBoard.setButtonId(move.substring(6));
        sendDataBoard.setSmallWin(board.isEnd(block));
        sendDataBoard.setBigWin(board.isEnd());
        Gson gson = new Gson();
        return gson.toJson(sendDataBoard);
    }

    private void parse (String data) {
        String[] str = data.split(" ");
        String name = str[1];
        LogClass.logCommunicate("GameThread: " + data);
        boolean isWin = str[2].equals("win");
        dB.upgrade(name, isWin);
        if (name.equals(nameUser1))
            name = nameUser2;
        else
            name = nameUser1;
        dB.upgrade(name, !isWin);
    }

    private void endGame() {
        LogClass.logCommunicate("GameThread: close game");
        WaitingThread waitingThread = new WaitingThread(socket2, server, nameUser2);
        server.serverList.addLast(waitingThread);
        WaitingThread waitingThread2 = new WaitingThread(socket1, server, nameUser1);
        server.serverList.addLast(waitingThread2);
        server.gameList.remove(this);
        this.interrupt();
    }

    private void sendToClient(String str, BufferedWriter bw) throws IOException {
        bw.write(str);
        bw.newLine();
        bw.flush();
    }
}