module com.example.tictactoe {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.json;
    requires com.google.gson;

    opens com.example.tictactoe to javafx.fxml, com.google.gson;
    opens IO to com.google.gson, javafx.controls;
    opens Model to javafx.fxml, com.google.gson, javafx.controls, javafx.base, javafx.graphics;
    opens Client to javafx.fxml;

    exports com.example.tictactoe;
    exports IO;
    exports Model;
}
