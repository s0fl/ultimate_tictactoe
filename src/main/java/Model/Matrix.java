package Model;

public class Matrix {
    private int[][] field;
    private int whoWin;
    private boolean isEnd;

    public Matrix() {
        field = new int[3][3];
        whoWin = 0;
        isEnd = false;
    }

    private boolean canMove(int x, int y) {
        if (x < 0 || x > 2 || y < 0 || y > 2)
            return false;
        if (field[x][y] == 0)
            return true;
        return false;
    }

    private boolean isCol(int y) {
        for (int i = 0; i < 2; i++) {
            if (field[i][y] == 0)
                return false;
            if (field[i][y] != field[i + 1][y])
                return false;
        }
        whoWin = field[0][y];
        isEnd = true;
        return true;
    }

    private boolean isRow(int x) {
        for (int y = 0; y < 2; y++) {
            if (field[x][y] == 0)
                return false;
            if (field[x][y] != field[x][y + 1])
                return false;
        }
        isEnd = true;
        whoWin = field[x][0];
        return true;
    }

    private boolean isDiag() {
        if (field[1][1] == 0)
            return false;
        if (field[1][1] == field[0][0]) {
            if (field[0][0] == field[2][2]) {
                whoWin = field[1][1];
                isEnd = true;
                return true;
            }
        }
        if (field[1][1] == field[0][2]) {
            if (field[0][2] == field[2][0]) {
                whoWin = field[1][1];
                isEnd = true;
                return true;
            }
        }
        return false;
    }

    public boolean isEnd() {
        if (isEnd)
            return true;
        for (int i = 0; i < 3; i++) {
            if (isRow(i))
                return true;
            if (isCol(i))
                return true;
        }
        if (isDiag())
            return true;
        if (isFull())
            return true;
        return false;
    }

    private boolean isFull() {
        int count = 0;
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                if (field[i][j] != 0)
                    count++;
        if (count == 9) {
            whoWin = 0;
            isEnd = true;
        }
        return false;
    }

    public int whoWin() {
        if (isEnd)
            return whoWin;
        isEnd();
        return whoWin;
    }

    public boolean setMove(int p, int x, int y) {
        if (!canMove(x, y))
            return false;
        field[x][y] = p;
        return true;
    }

    public String getRow(int x) {
        StringBuilder m = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            m.append(field[x][i]).append(" ");
        }
        m.append("\t");
        return m.toString();
    }

    public int get(int x, int y) {
        return field[x][y];
    }

    public String toString() {
        StringBuilder m = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++)
                m.append(field[i][j]).append(" ");
            m.append("\n");
        }
        return m.toString();
    }
}

