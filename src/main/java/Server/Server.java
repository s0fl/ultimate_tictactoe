package Server;

import Logger.LogClass;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

public class Server {
    private ServerSocket serverSocket;
    public LinkedList<WaitingThread> serverList = new LinkedList<>();
    public LinkedList<GameThread> gameList = new LinkedList<>();
    private StartServer startServer;

    public StartServer getStartServer() {
        return startServer;
    }

    public Server() {
        startServer = new StartServer();
    }

    public Server getServer() {
        return this;
    }

    public class StartServer extends Thread {
        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(2122);
                LogClass.setLog("server running");
                LogClass.logCommunicate("server running");
                while (!Thread.currentThread().isInterrupted()) {
                    if (!serverSocket.isClosed()) {
                        Socket socketOne = serverSocket.accept();
                        LogClass.logCommunicate("client connected" + socketOne.getInetAddress() + socketOne.getPort());
                        WaitingThread waitingThread = new WaitingThread(socketOne, getServer());
                        waitingThread.start();
                    }
                }
            } catch (IOException e) {
                LogClass.logError(StartServer.class, e.getMessage());
                e.printStackTrace();

            }
        }
    }

    public void stopServer() throws IOException {
        if (!serverSocket.isClosed()) {
            for (int i = 0; i < serverList.size(); i++) {
                serverList.get(i).interrupt();
            }
            for (int i = 0; i < gameList.size(); i++) {
                gameList.get(i).interrupt();
            }
            this.startServer.interrupt();
            serverSocket.close();
        }
    }
}
