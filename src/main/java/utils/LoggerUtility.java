package utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.*;


public class LoggerUtility {
    private static final Logger logger = Logger.getLogger(LoggerUtility.class.getName());
    private static final String LOG_FILE = "logs/goatraceclub.log";

    static {
        try {
            Files.createDirectories(Paths.get("logs"));
            FileHandler fileHandler = new FileHandler(LOG_FILE, true);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);

            // Tilføj også console handler for at vise logs i konsollen
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(consoleHandler);

            logger.setLevel(Level.ALL); // Log alle niveauer
        } catch (IOException e) {
            System.err.println("Kunne ikke initialisere logfil: " + e.getMessage());
        }
    }

    public static void logEvent(String message) {
        String logMessage = formatMessage("EVENT", message);
        logger.info(logMessage);
        writeToLogFile(logMessage);
        System.out.println(logMessage); // Vis også i konsollen
    }

    public static void logError(String message) {
        String logMessage = formatMessage("ERROR", message);
        logger.severe(logMessage);
        writeToLogFile(logMessage);
        System.err.println(logMessage); // Vis fejl i konsollen med rød tekst
    }

    public static void logError(String message, Throwable e) {
        String exceptionDetails = e.getMessage() + "\n";
        for (StackTraceElement element : e.getStackTrace()) {
            exceptionDetails += "    at " + element.toString() + "\n";
        }

        String logMessage = formatMessage("ERROR", message + "\n" + exceptionDetails);
        logger.severe(logMessage);
        writeToLogFile(logMessage);
        System.err.println(logMessage); // Vis fejl i konsollen med rød tekst

        // Hvis der er en "cause", log den også
        if (e.getCause() != null) {
            logError("Forårsaget af: " + e.getCause().getMessage(), e.getCause());
        }
    }

    public static void logWarning(String message) {
        String logMessage = formatMessage("WARNING", message);
        logger.warning(logMessage);
        writeToLogFile(logMessage);
        System.out.println(logMessage); // Vis også i konsollen
    }


    public static void logDatabaseOperation(String operation, String details) {
        String logMessage = formatMessage("DATABASE", operation + ": " + details);
        logger.info(logMessage);
        writeToLogFile(logMessage);
    }


    private static String formatMessage(String type, String message) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return String.format("[%s] %s: %s", timestamp, type, message);
    }


    private static void writeToLogFile(String message) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE, true))) {
            writer.write(message);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Kunne ikke skrive til logfil: " + e.getMessage());
        }
    }
}