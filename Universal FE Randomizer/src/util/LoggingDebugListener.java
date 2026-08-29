package util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class LoggingDebugListener implements DebugListener {

    BufferedWriter bufferedWriter;

    public LoggingDebugListener(String fileName) throws IOException {
        bufferedWriter = new BufferedWriter(new FileWriter(fileName));
    }

    @Override
    public void logMessage(String category, String message) {

        if (bufferedWriter == null || DebugPrinter.Key.HUFFMAN.label.equals(category)) {
            return;
        }

        try {
            bufferedWriter.write(String.format("%s - %s%n", category, message));
        } catch (IOException e) {
            System.out.println("logging failed. message: " +message);
        }
    }
}