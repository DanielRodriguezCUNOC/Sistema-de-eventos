package com.hyrule.Backend.archiveprocessor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import com.hyrule.Backend.LogFormatter;
import com.hyrule.Backend.connection.DBConnection;
import com.hyrule.Backend.handler.ActivityRegisterHandler;
import com.hyrule.Backend.handler.AttendanceRegisterHandler;
import com.hyrule.Backend.handler.CertifiedRegisterHandler;
import com.hyrule.Backend.handler.EventRegisterHandler;
import com.hyrule.Backend.handler.InscripcionRegisterHandler;
import com.hyrule.Backend.handler.ParticipantRegisterHandler;
import com.hyrule.Backend.handler.PaymentRegisterHandler;
import com.hyrule.Backend.handler.ValidateInscripcionRegisterHandler;
import com.hyrule.interfaces.RegisterHandler;

public class ProcessorArchive {

    private final Map<String, RegisterHandler> HANDLER = new HashMap<>();

    private ScheduledExecutorService executor;
    private volatile boolean isRunning = false;
    private Path fileLogGenerado;

    DBConnection dbConnection;
    Connection conn;

    public ProcessorArchive() {

        // * Mapeo de tipos de eventos a sus respectivos manejadores */

        HANDLER.put("REGISTRO_EVENTO", new EventRegisterHandler(getConnection()));

        HANDLER.put("REGISTRO_PARTICIPANTE", new ParticipantRegisterHandler(getConnection()));

        HANDLER.put("REGISTRO_ACTIVIDAD", new ActivityRegisterHandler(getConnection()));

        HANDLER.put("INSCRIPCION", new InscripcionRegisterHandler(getConnection()));

        HANDLER.put("CERTIFICADO", new CertifiedRegisterHandler(getConnection()));

        HANDLER.put("VALIDAR_INSCRIPCION", new ValidateInscripcionRegisterHandler(getConnection()));

        HANDLER.put("ASISTENCIA", new AttendanceRegisterHandler(getConnection()));

        HANDLER.put("PAGO", new PaymentRegisterHandler(getConnection()));

        // * Inicializamos la conexión a la base de datos */
        this.dbConnection = new DBConnection();
        try {
            this.conn = dbConnection.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void processWithThread(Path filePath, Path logFilePath, int delay, Runnable onComplete) {

        if (isRunning) {
            throw new IllegalStateException("El procesador ya está en ejecución.");
        }

        Path fileLog = generateFile(logFilePath);

        try {
            Files.createDirectories(fileLog);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo crear el directorio para los logs." + fileLog, e);

        }

        executor = Executors.newSingleThreadScheduledExecutor();
        isRunning = true;

        executor.execute(() -> {
            LogFormatter logFormatter = null;
            try (BufferedReader br = new BufferedReader(new FileReader(filePath.toFile()));
                    BufferedWriter logWriter = new BufferedWriter(new FileWriter(fileLog.toFile(), true))) {

                logFormatter = new LogFormatter(logWriter);

                String linea;
                while ((linea = br.readLine()) != null && !Thread.currentThread().isInterrupted()) {

                    linea = linea.trim();
                    if (linea.isEmpty())
                        continue;

                    procesarArchivo(linea, logWriter);
                    Thread.sleep(delay);

                }

            } catch (Exception e) {
                try {
                    if (logFormatter != null) {
                        logFormatter.error("Error al procesar el archivo: " + e.getMessage());
                    }
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                e.printStackTrace();
            } finally {
                isRunning = false;
                if (onComplete != null) {
                    onComplete.run();
                }
            }
        });

        executor.shutdown();

    }

    private void procesarArchivo(String linea, BufferedWriter logWriter) throws Exception {
        LogFormatter logFormatter = new LogFormatter(logWriter);
        String comando = extraerComando(linea);
        RegisterHandler handler = HANDLER.get(comando);

        if (handler != null) {
            boolean estado = handler.process(linea, logWriter);
            if (!estado) {
                logFormatter.error("Error al procesar la línea: " + linea);
            } else {
                logFormatter.info("Línea procesada correctamente: " + linea);
            }
        } else {
            logFormatter.error("Comando no reconocido: " + comando + " en la línea: " + linea);
        }

    }

    private String extraerComando(String linea) {
        // *Hallamos la posicion del primer parentesis */
        int idx = linea.indexOf("(");
        // *Extraemos la subcadena desde el inicio hasta el parentesis*/
        // *caso contrario devolvemos la linea completa */
        return (idx > 0) ? linea.substring(0, idx) : linea;
    }

    public Connection getConnection() {
        return conn;
    }

    // Método para detener el procesamiento
    public void stopProcessing() {
        if (executor != null) {
            executor.shutdownNow();
            isRunning = false;
        }
    }

    // Verifica si está procesando
    public boolean isProcessing() {
        return isRunning;
    }

    public Path generateFile(Path carpetaLog) {

        String fileLog = "log_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".log";
        setFileLogGenerado(carpetaLog.resolve(fileLog));
        return carpetaLog.resolve(fileLog);
    }

    public void setFileLogGenerado(Path fileLogGenerado) {
        this.fileLogGenerado = fileLogGenerado;
    }

    public Path getFileLogGenerado() {
        return fileLogGenerado;
    }
}
