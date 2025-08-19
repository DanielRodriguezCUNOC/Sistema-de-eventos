package com.hyrule.Backend;

import java.io.BufferedWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Formateador de logs para escribir mensajes con timestamp
 */
public class LogFormatter {
    private final BufferedWriter WRITER;
    private final DateTimeFormatter TIMESTAMPFORMATTER;

    public LogFormatter(BufferedWriter WRITER) {
        this.WRITER = WRITER;
        this.TIMESTAMPFORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    }

    public void info(String message) throws IOException {
        write("INFO", message);
    }

    public void error(String message) throws IOException {
        write("ERROR", message);
    }

    private void write(String level, String message) throws IOException {
        WRITER.write(String.format("[%s] [%s] %s",
                LocalDateTime.now().format(TIMESTAMPFORMATTER),
                level,
                message));
        WRITER.newLine();
        WRITER.flush();
    }
}
