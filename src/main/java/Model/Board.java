package Model;

import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Scanner;

public class Board {
    private LinkedList<Matrix> board;
    private Matrix winners;
    int x = 0;
    int y = 0;
    int x_small;
    int y_small;
    boolean player = false;

    public Board(){
        board = new LinkedList<Matrix>();
        for(int i = 0; i < 9; i++)
            board.add(new Matrix());
        winners = new Matrix();
        x = -1;
        y = -1;
    }

    public boolean isEnd() {
        return winners.isEnd();
    }

    public boolean isEnd(int b){
        int bx = b/3;
        int by = b%3;
//        System.out.println("["+x+"; " + y +"]");
        return isEnd(bx, by);
    }

    public boolean isEnd(int x, int y){
        if(winners.get(x, y) > 0)
            return true;
        else
            return false;
    }

    public int smallWin() {
        if (x_small > 0 && y_small > 0)
            return winners.get(x_small, y_small);
        else return 0;
    }

    public int whoWin() {
        return winners.whoWin();
    }

    public boolean setMove(int p, int b, int i, int j) {
        Matrix buf = board.get(b);
        if (!buf.setMove(p, i, j)) {
            return false;
        }
        if (buf.isEnd()) {
            x = b / 3;
            y = b % 3;
            winners.setMove(buf.whoWin(), x, y);
            System.out.println(winners.toString());
        }
        x = i;
        y = j;
        board.set(b, buf);
        return true;
    }

    public int getNextSmallField() {
        return x * 3 + y;
    }

    public String toString() {
        StringBuilder b = new StringBuilder();
        ListIterator<Matrix> it = board.listIterator();
        for(int i = 0; i < 3; i++){
            Matrix[] buf = new Matrix[3];
            for(int j = 0; j < 3; j++) {
                buf[j] = it.next();
            }
            for(int j = 0; j < 3; j++) {
                for (int k = 0; k < 3; k++)
                    b.append(buf[k].getRow(j));
                b.append("\n");
            }
            b.append("\n");
        }
        return b.toString();
    }

    public LinkedList<Matrix> getBoard() {
        return board;
    }

    public boolean isPlayer() {
        return player;
    }

    public int game(String buttonId) {
        if (!isEnd()) {
            int p;
            if (player)
                p = 2;
            else
                p = 1;
            if (buttonId != null) {
                x_small = x;
                y_small = y;
                String[] position = buttonId.split("_");
                int b = Integer.parseInt(position[1]);
                int x = Integer.parseInt(position[2]);
                int y = Integer.parseInt(position[3]);
                setMove(p, b, x, y);
                System.out.println(winners.toString());
                player = !player;
                return b;
            }
        }
        else
            System.out.println("The winner is the player " + whoWin());
        return -1;
    }
}
