package com.hyrule.Backend.handler;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import com.hyrule.Backend.LogFormatter;
import com.hyrule.Backend.RegularExpresion.RExpresionCertificado;
import com.hyrule.Backend.Validation.ValidationArchive;
import com.hyrule.Backend.model.certified.CertifiedModel;
import com.hyrule.Backend.persistence.certified.ControlCertified;
import com.hyrule.Backend.reports.CertifiedCreator;
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

    CertifiedCreator certifiedCreator;
    private Path filePath;

    /**
     * Constructor que recibe la conexión a la base de datos.
     */
    public CertifiedRegisterHandler(Connection conn, Path filePath) {
        this.conn = conn;
        this.certifiedCreator = new CertifiedCreator();
        this.filePath = filePath;
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
    public boolean process(String linea, LogFormatter logWriter) {

        try {
            RExpresionCertificado parser = new RExpresionCertificado();
            CertifiedModel certified = parser.parseCertified(linea.trim());
            if (certified == null) {
                logWriter.error("Línea inválida o incompleta: " + linea);
                return false;
            }

            // *Validamos si el certificado ya existe */
            if (validator.existsCertificado(certified.getCorreoParticipante(), certified.getCodigoEvento())) {
                logWriter.error("El certificado ya existe para el participante: " + certified.getCorreoParticipante()
                        + " en el evento: " + certified.getCodigoEvento());
                return false;
            }

            if (control.insert(certified, conn) != null) {
                validator.addCertificado(certified);
                if (!"Ok".equals(certifiedCreator.createCertificateFromArchive(certified.getCorreoParticipante(),
                        certified.getCodigoEvento(), filePath))) {
                    logWriter.error("Error al generar el certificado para: " + certified.getCorreoParticipante());
                    return false;
                }
                logWriter.info("Certificado registrado y generado: " + certified);
            }
            return true;

        } catch (Exception e) {
            try {
                logWriter.error("Excepción procesando VALIDAR_INSCRIPCION: " + e.getMessage());
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
    public boolean insertFromForm(CertifiedModel certified, Path filePath) {
        try {
            if (control.insert(certified, conn) != null) {
                certifiedCreator.createCertificateFromForm(
                        certified.getCodigoEvento(), certified.getCorreoParticipante(), conn, control, filePath);
                return true;
            } else {
                return false;
            }
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
