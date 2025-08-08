package com.hyrule.Backend.archiveprocessor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import com.hyrule.Backend.handler.EventRegisterHandler;
import com.hyrule.interfaces.RegisterHandler;

public class ProcessorArchive {

    private final Map<String, RegisterHandler> handlers = new HashMap<>();

    public ProcessorArchive() {

        // * Mapeo de tipos de eventos a sus respectivos manejadores */
        handlers.put("REGISTRO_EVENTO", new EventRegisterHandler());
    }

    public void procesarArchivo(Path filePath) {
        Path logPath = Path.of("Errores_procesamiento.log");

        try (BufferedReader br = new BufferedReader(new FileReader(filePath.toFile()));
                BufferedWriter logWriter = new BufferedWriter(new FileWriter(logPath.toFile(), true))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim(); // * Eliminar espacios en blanco */
                if (linea.isEmpty()) {
                    continue; // * Ignorar líneas vacías */
                }
                String comando = extraerComando(linea);
                RegisterHandler handler = handlers.get(comando);

                if (handler != null) {
                    boolean estado = handler.process(linea, logWriter);
                    if (!estado) {
                        logWriter.write("Error procesando comando: " + comando + " en la línea: " + linea);
                        logWriter.newLine();
                    }
                } else {
                    logWriter.write("Comando no reconocido: " + comando + " en la línea: " + linea);
                    logWriter.newLine();
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String extraerComando(String linea) {
        int idx = linea.indexOf("(");
        return (idx > 0) ? linea.substring(0, idx) : linea;
    }
}
