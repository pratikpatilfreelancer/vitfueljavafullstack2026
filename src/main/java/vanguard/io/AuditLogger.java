package vanguard.io;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Appends human-readable log lines to a text file, and can read them
 * back. Uses try-with-resources (ARM — Automatic Resource Management)
 * throughout so writers/readers are always closed even if an
 * exception occurs mid-write.
 */
public class AuditLogger {

    private static final String LOG_FILE = "vanguard_audit.log";
    private static final DateTimeFormatter TS =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void log(String message) {
        StringBuilder line = new StringBuilder();
        line.append('[').append(LocalDateTime.now().format(TS)).append("] ").append(message);

        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(LOG_FILE, true))) {
            writer.write(line.toString());
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Failed to write audit log: " + e.getMessage());
        }
    }

    public void printLog() {
        System.out.println(readLog());
    }

    /** Same content as printLog(), returned as a String for GUI use. */
    public String readLog() {
        Path path = Paths.get(LOG_FILE);
        if (!Files.exists(path)) {
            return "(no audit log yet — nothing has been recorded)";
        }
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(LOG_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            return "Failed to read audit log: " + e.getMessage();
        }
        return sb.toString();
    }
}
