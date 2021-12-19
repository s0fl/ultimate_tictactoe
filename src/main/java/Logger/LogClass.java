package Logger;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LogClass {
    private final static Logger LOGGER = Logger.getLogger("nescent");

    public static void logError(Class classInWithError, String info) {
        LOGGER.log(Level.WARNING, classInWithError.getSimpleName() + "-> Error: " + info);
    }

    public static void logCommunicate(String info) {
        LOGGER.info(info);
    }

    public static void setLog(String str) {
        LOGGER.setLevel(Level.ALL);
        LOGGER.info(str);
        try {
            FileHandler fileHandler = new FileHandler("Logfile.txt");
            SimpleFormatter formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);
            LOGGER.fine("information");
            LOGGER.addHandler(fileHandler);

        } catch (SecurityException | IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }
}