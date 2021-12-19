package Model;

public class Table {
    int id;
    String name;
    int win;
    int lose;
    int percent;

    public Table() {}

    public Table(int id, String name, int win, int lose, int percent) {
        this.id = id;
        this.name = name;
        this.win = win;
        this.lose = lose;
        this.percent = percent;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getWin() {
        return win;
    }

    public int getLose() {
        return lose;
    }

    public int getPercent() {
        return percent;
    }
}
