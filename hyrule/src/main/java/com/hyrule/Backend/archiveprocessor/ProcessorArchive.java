package com.hyrule.Backend.archiveprocessor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Path;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

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
                RegisterHandler handler = HANDLER.get(comando);

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

    public Connection getConnection() {
        return conn;
    }
}
