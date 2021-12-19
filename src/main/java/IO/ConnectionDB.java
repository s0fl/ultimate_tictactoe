package IO;

import Model.Rating;
import Model.Table;

import java.sql.*;

public class ConnectionDB {
    public Connection connection;
    private Rating rating;

    public Connection getConnection() {
        String user = "root";
        String password = "qweasd123";
        String connectionUrl = "jdbc:mysql://localhost:3306/test";

        try {
            connection = DriverManager.getConnection(connectionUrl, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void upgrade(String name, boolean win) {
        ConnectionDB connectionDB = new ConnectionDB();

        try (Connection connection = connectionDB.getConnection()) {
            String verify = "select *from rate where name='" + name + "'";
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(verify);

            int w = 0;
            int l = 0;
            if (rs.next()) {
                w = rs.getInt(3);
                l = rs.getInt(4);
                if (win) {
                    w = rs.getInt(3) + 1;
                } else {
                    l = rs.getInt(4) + 1;
                }
            }
            statement.executeUpdate("delete from rate where name='" + name + "'");
            statement.executeUpdate("insert into rate(name, win, lose) values('" + name + "'," + w + "," + l + ");");

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            connectionDB.close();
        }
    }

    public boolean checkLogin(String login, String pass) {
        ConnectionDB connectionDB = new ConnectionDB();

        try (Connection connection = connectionDB.getConnection()) {
            String verify = "select count(1) from users where name='" + login + "' and password='" + pass + "'";
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(verify);

            while (rs.next()) {
                if (rs.getInt(1) == 1) {
                    return true;
                }
            }

        } catch (SQLException e) {
            return false;
        } finally {
            connectionDB.close();
        }
        return false;
    }

    public boolean checkRegistration(String login, String pass) {
        ConnectionDB connectionDB = new ConnectionDB();

        try (Connection connection = connectionDB.getConnection()) {
            boolean exists = false;
            String verify = "select count(1) from users where name='" + login + "'";
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(verify);

            while (rs.next()) {
                if (rs.getInt(1) == 1) {
                    exists = true;
                }
            }
            if (!exists) {
                statement.executeUpdate("insert into users(name, password) values('" + login + "'," + pass + ");");
                statement.executeUpdate("insert into rate(name, win, lose) values('" + login + "'," + 0 + "," + 0 + ");");
                return true;
            }
        } catch (SQLException e) {
            return false;
        } finally {
            connectionDB.close();
        }
        return false;
    }

    public Model.Rating loadRate() {
        try {
            rating = new Rating();
            ConnectionDB db = new ConnectionDB();
            Connection connection = db.getConnection();
            ResultSet rs = connection.createStatement().executeQuery("select name, win, lose, (win / (lose + win) * 100) as winrate from rate order by winrate desc;");

            int i = 1;
            while (rs.next()) {
                rating.getTables().add(new Table(i, rs.getString("name"), rs.getInt("win"), rs.getInt("lose"), rs.getInt("winrate")));
                i++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rating;
    }
}
