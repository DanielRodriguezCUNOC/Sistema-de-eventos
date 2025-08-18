package com.hyrule.Backend.handler;

import java.io.BufferedWriter;
import java.sql.Connection;
import java.util.regex.Pattern;

import com.hyrule.Backend.RegularExpresion.RExpresionInscripcion;
import com.hyrule.Backend.Validation.ValidationArchive;
import com.hyrule.Backend.model.inscripcion.RegistrationModel;
import com.hyrule.Backend.persistence.inscripcion.ControlRegistration;
import com.hyrule.interfaces.RegisterHandler;

/**
 * Manejador para el registro de inscripciones desde archivos y formularios.
 * Procesa validaciones, duplicados y persistencia de inscripciones.
 */
public class InscripcionRegisterHandler implements RegisterHandler {

    /** Controlador de persistencia para operaciones de inscripciones */
    private ControlRegistration control = new ControlRegistration();

    /** Conexión a la base de datos. */
    private Connection conn;

    /** Constructor que recibe la conexión a la base de datos. */
    public InscripcionRegisterHandler(Connection conn) {
        this.conn = conn;
    }

    /** Validador singleton para verificar duplicados */
    ValidationArchive validator = ValidationArchive.getInstance();

    /**
     * Procesa una línea de inscripción desde archivo de carga masiva.
     * 
     * @param linea     línea del archivo con datos de la inscripción
     * @param logWriter escritor para registrar errores y éxitos
     * @return true si la inscripción se procesó correctamente
     */
    @Override
    public boolean process(String linea, BufferedWriter logWriter) {
        try {

            // *Expresion regular para validar la linea */
            RExpresionInscripcion parser = new RExpresionInscripcion();
            RegistrationModel inscripcion = parser.parseRegistration(linea.trim());

            if (inscripcion == null) {
                logWriter.write("Línea inválida o incompleta: " + linea);
                logWriter.newLine();
                return false;
            }
            if (validator.existsRegistration(inscripcion.getCodigoEvento(), inscripcion.getCorreoParticipante())) {
                logWriter.write("La inscripción ya existe: " + inscripcion.getCodigoEvento() + " - "
                        + inscripcion.getCorreoParticipante());
                logWriter.newLine();
                return false;
            }

            // *Agregamos la inscripción a la estructura de validación */
            validator.addInscripcion(inscripcion);

            logWriter.write("Inscripción registrada: " + inscripcion);
            logWriter.newLine();

            // *Insertamos la inscripción en la base de datos */
            return control.insert(inscripcion, conn) != null;

        } catch (Exception e) {
            try {
                logWriter.write("Excepción procesando INSCRIPCION: " + e.getMessage());
                logWriter.newLine();
            } catch (Exception ignore) {
            }
            return false;
        }
    }

    /**
     * Inserta una inscripción directamente desde formulario.
     * 
     * @param inscripcion la inscripción a insertar
     * @return true si se insertó correctamente
     */
    public boolean insertFromForm(RegistrationModel inscripcion) {
        try {
            return control.insert(inscripcion, conn) != null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Valida los datos de una inscripción desde formulario.
     * 
     * @param inscripcion la inscripción a validar
     * @return "Ok" si es válida, mensaje de error si no
     */
    public String validateForm(RegistrationModel inscripcion) {
        try {
            if (inscripcion == null) {
                return "Inscripción nula";
            }

            // *Validamos la integridad de los datos */
            if (!validateDataIntegrity(inscripcion)) {
                return "Datos de inscripción inválidos";
            }

            String validation = control.validateInscripcion(inscripcion, conn);

            if (!"Ok".equals(validation)) {
                return validation;
            }

        } catch (Exception e) {
            return "Error en la validación: " + e.getMessage();
        }
        return "Ok";
    }

    /**
     * Valida la integridad de los datos de inscripción usando expresiones
     * regulares.
     * 
     * @param inscripcion la inscripción a validar
     * @return true si todos los datos son válidos
     */
    private boolean validateDataIntegrity(RegistrationModel inscripcion) {

        boolean codigoEvento = inscripcion.getCodigoEvento() != null &&
                inscripcion.getCodigoEvento().matches("^EVT-\\d{8}$") && inscripcion.getCodigoEvento().isBlank();
        boolean correoParticipante = inscripcion.getCorreoParticipante() != null &&
                inscripcion.getCorreoParticipante().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
                && inscripcion.getCorreoParticipante().isBlank();
        boolean tipoInscripcion = inscripcion.getTipoInscripcion() != null &&
                Pattern.matches("^(ASISTENTE|CONFERENCISTA|TALLERISTA|OTRO)$",
                        inscripcion.getTipoInscripcion().name())
                && inscripcion.getTipoInscripcion().name().isBlank();

        return codigoEvento && correoParticipante && tipoInscripcion;
    }

}
