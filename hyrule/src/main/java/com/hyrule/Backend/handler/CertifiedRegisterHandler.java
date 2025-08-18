package com.hyrule.Backend.handler;

import java.io.BufferedWriter;
import java.sql.Connection;
import java.sql.SQLException;

import com.hyrule.Backend.RegularExpresion.RExpresionCertificado;
import com.hyrule.Backend.Validation.ValidationArchive;
import com.hyrule.Backend.model.certified.CertifiedModel;
import com.hyrule.Backend.persistence.certified.ControlCertified;
import com.hyrule.interfaces.RegisterHandler;

/**
 * Manejador para el registro de certificados desde archivos y formularios.
 * Procesa validaciones, duplicados y persistencia de certificados.
 */
public class CertifiedRegisterHandler implements RegisterHandler {

    /** Controlador de persistencia para operaciones de certificados */
    public ControlCertified control = new ControlCertified();

    /** Conexión a la base de datos. */
    private Connection conn;

    /**
     * Constructor que recibe la conexión a la base de datos.
     */
    public CertifiedRegisterHandler(Connection conn) {
        this.conn = conn;
    }

    /** Validador singleton para verificar duplicados */
    ValidationArchive validator = ValidationArchive.getInstance();

    /**
     * Procesa una línea de certificado desde archivo de carga masiva.
     * 
     * @param linea     línea del archivo con datos del certificado
     * @param logWriter escritor para registrar errores y éxitos
     * @return true si el certificado se procesó correctamente
     */
    @Override
    public boolean process(String linea, BufferedWriter logWriter) {
        try {

            RExpresionCertificado parser = new RExpresionCertificado();
            CertifiedModel certified = parser.parseCertified(linea.trim());
            if (certified == null) {
                logWriter.write("Línea inválida o incompleta: " + linea);
                logWriter.newLine();
                return false;
            }

            // *Validamos si el certificado ya existe */
            if (validator.existsCertificado(certified.getCorreoParticipante(), certified.getCodigoEvento())) {
                logWriter.write("El certificado ya existe para el participante: " + certified.getCorreoParticipante()
                        + " en el evento: " + certified.getCodigoEvento());
                logWriter.newLine();
                return false;
            }

            // *Agregamos los datos al HashSet */
            validator.addCertificado(certified);

            // * Insertamos el certificado en la base de datos */
            return control.insert(certified, conn) != null;

        } catch (Exception e) {
            try {
                logWriter.write("Excepción procesando VALIDAR_INSCRIPCION: " + e.getMessage());
                logWriter.newLine();
            } catch (Exception ignore) {
            }
            return false;
        }
    }

    /**
     * Inserta un certificado directamente desde formulario.
     * 
     * @param certified el certificado a insertar
     * @return true si se insertó correctamente
     */
    public boolean insertFromForm(CertifiedModel certified) {
        try {
            return control.insert(certified, conn) != null;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Valida los datos de un certificado desde formulario.
     * 
     * @param certified el certificado a validar
     * @return "Ok" si es válido, mensaje de error si no
     */
    public String validateForm(CertifiedModel certified) {

        if (certified == null) {
            return "Certificado nulo.";
        }

        if (!validateDataIntegrity(certified)) {
            return "Datos del certificado inválidos.";
        }

        String validationMessage = control.validateCertificate(certified, conn);
        if (!validationMessage.equals("Ok")) {
            return validationMessage;
        }

        return "Ok";

    }

    /**
     * Valida la integridad de los datos del certificado usando expresiones
     * regulares.
     * 
     * @param certified el certificado a validar
     * @return true si todos los datos son válidos
     */
    private boolean validateDataIntegrity(CertifiedModel certified) {

        boolean codigoEventoValido = certified.getCodigoEvento() != null &&
                certified.getCodigoEvento().matches("^EVT-\\d{8}$") && !certified.getCodigoEvento().isBlank();

        boolean correoValido = certified.getCorreoParticipante() != null &&
                certified.getCorreoParticipante().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
                && !certified.getCorreoParticipante().isBlank();

        return codigoEventoValido && correoValido;
    }

}
