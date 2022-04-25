package flashcards;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Logger {

    private final List<String> logs = new ArrayList<>();
    private final Scanner scanner = new Scanner(System.in);

    protected List<String> getLogs() {
        return logs;
    }

    protected void println(String message) {
        String logMessage = String.format("%s - %s\n", getTime(), message);
        this.logs.add(logMessage);
        System.out.println(message);
    }

    protected void printf(String format, Object... parameters) {
        String logMessage = String.format("%s - %s\n", getTime(), format);
        logs.add(logMessage);
        System.out.printf(format, parameters);
    }

    protected static String getTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;
        Instant instant = Instant.now();
        return formatter.format(instant);
    }

    protected String nextLine() {
        String input = scanner.nextLine();
        logs.add(String.format("%s - %s\n", getTime(), input));
        return input;
    }

    public void saveLogsToFile() {
        this.println("File name:");
        String fileName = this.nextLine().trim();
        File file = new File(fileName);

        try (FileWriter fileWriter = new FileWriter(file.getAbsoluteFile());
             BufferedWriter bw = new BufferedWriter(fileWriter)) {
            for (String logLine : this.logs) {
                bw.write(logLine);
            }
            String message = "The log has been saved.";
            this.println(message);
            bw.write(String.format("%s - %s", Logger.getTime(), message));
        } catch (IOException e) {
            e.printStackTrace();
            String error = "Exception, Logs were not saved.";
            this.println(error);
        }

/*        try (FileOutputStream fileOut = new FileOutputStream(fileName);
             ObjectOutputStream objOut = new ObjectOutputStream(fileOut)) {
            objOut.writeObject(currentLogs);
            logger.println("The log has been saved.");
        } catch (FileNotFoundException e) {
            logger.println("File not found.");
        } catch (IOException e) {
            logger.println(e.getClass().getName());
        }*/
    }
}
