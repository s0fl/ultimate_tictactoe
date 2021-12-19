package com.example.tictactoe;

import java.io.IOException;
import static java.lang.System.exit;

public class ClientApp {
    public static void main(String[] args) {
        try {
            HelloApplication.main(args);
        } catch (IOException e) {
            e.printStackTrace();
        }
        exit(0);
    }
}
