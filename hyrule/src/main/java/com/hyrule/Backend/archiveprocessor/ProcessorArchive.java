package com.hyrule.Backend.archiveprocessor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

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

    private final Map<String, RegisterHandler> handlers = new HashMap<>();

    public ProcessorArchive() {

        // * Mapeo de tipos de eventos a sus respectivos manejadores */

        handlers.put("REGISTRO_EVENTO", new EventRegisterHandler());

        handlers.put("REGISTRO_PARTICIPANTE", new ParticipantRegisterHandler());

        handlers.put("REGISTRO_ACTIVIDAD", new ActivityRegisterHandler());

        handlers.put("INSCRIPCION", new InscripcionRegisterHandler());

        handlers.put("CERTIFICADO", new CertifiedRegisterHandler());

        handlers.put("VALIDAR_INSCRIPCION", new ValidateInscripcionRegisterHandler());

        handlers.put("ASISTENCIA", new AttendanceRegisterHandler());

        handlers.put("PAGO", new PaymentRegisterHandler());

    }

    public void procesarArchivo(Path filePath) {
        Path logPath = Path.of("Errores_procesamiento.log");

        try (BufferedReader br = new BufferedReader(new FileReader(filePath.toFile()));
                BufferedWriter logWriter = new BufferedWriter(new FileWriter(logPath.toFile(), true))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                // * Eliminar espacios en blanco */
                linea = linea.trim();
                // * Ignorar líneas vacías */
                if (linea.isEmpty()) {
                    continue;
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
        // *Hallamos la posicion del primer parentesis */
        int idx = linea.indexOf("(");
        // *Extraemos la subcadena desde el inicio hasta el parentesis*/
        // *caso contrario devolvemos la linea completa */
        return (idx > 0) ? linea.substring(0, idx) : linea;
    }
}
