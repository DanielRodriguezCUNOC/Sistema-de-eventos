package com.hyrule.Backend;

import java.io.BufferedWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogFormatter {
    private final BufferedWriter writer;
    private final DateTimeFormatter timestampFormat;

    public LogFormatter(BufferedWriter writer) {
        this.writer = writer;
        this.timestampFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    }

    public void info(String message) throws IOException {
        write("INFO", message);
    }

    public void error(String message) throws IOException {
        write("ERROR", message);
    }

    private void write(String level, String message) throws IOException {
        writer.write(String.format("[%s] [%s] %s",
                LocalDateTime.now().format(timestampFormat),
                level,
                message));
        writer.newLine();
        writer.flush();
    }
}
