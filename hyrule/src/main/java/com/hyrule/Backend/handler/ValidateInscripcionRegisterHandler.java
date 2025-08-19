package com.hyrule.Backend.handler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.regex.Pattern;

import com.hyrule.Backend.LogFormatter;
import com.hyrule.Backend.RegularExpresion.RExpresionValidarInscripcion;
import com.hyrule.Backend.Validation.ValidationArchive;
import com.hyrule.Backend.model.validate_registration.ValidateRegistrationModel;
import com.hyrule.Backend.persistence.validate_registration.ControlValidateRegistration;
import com.hyrule.interfaces.RegisterHandler;

/**
 * Manejador para el procesamiento y validación de inscripciones de
 * participantes en eventos.
 * 
 * Esta clase se encarga de procesar las líneas de validación de inscripciones
 * desde archivos,
 * validar que los participantes estén correctamente inscritos y hayan realizado
 * los pagos
 * correspondientes antes de confirmar su inscripción en el evento.
 * 
 * @author Paboomi
 * @version 1.0
 * @since 2025
 */
public class ValidateInscripcionRegisterHandler implements RegisterHandler {

    /**
     * Controlador para las operaciones de persistencia de validaciones de
     * inscripción.
     */
    private ControlValidateRegistration control = new ControlValidateRegistration();

    /**
     * Conexión a la base de datos.
     */
    Connection conn;

    /**
     * Constructor que recibe la conexión a la base de datos.
     * 
     * @param conn la conexión a la base de datos
     */
    public ValidateInscripcionRegisterHandler(Connection conn) {
        this.conn = conn;
    }

    /**
     * Instancia del validador de archivos para verificar la integridad de los
     * datos.
     */
    ValidationArchive validator = ValidationArchive.getInstance();

    /**
     * Procesa una línea de texto que contiene información de validación de
     * inscripción.
     * 
     * Este método analiza la línea utilizando expresiones regulares, valida que el
     * participante
     * esté inscrito en el evento, que haya realizado el pago correspondiente, y que
     * no exista
     * una validación duplicada antes de procesar la inscripción.
     * 
     * @param linea     El texto de la línea a procesar que contiene los datos de
     *                  validación
     * @param logWriter El escritor para registrar logs del proceso de validación
     * @return true si la validación se procesó exitosamente, false en caso
     *         contrario
     * @throws Exception Si ocurre un error durante el procesamiento
     */
    @Override
    public boolean process(String linea, LogFormatter logWriter) {
        try {

            // *Validamos usando expresion regular */

            RExpresionValidarInscripcion parser = new RExpresionValidarInscripcion();
            ValidateRegistrationModel inscripcion = parser.parseValidateRegistration(linea.trim());

            if (inscripcion == null) {
                logWriter.error("Línea inválida o incompleta: " + linea);
                return false;
            }

            if (!validator.existsRegistration(inscripcion.getCorreo(), inscripcion.getCodigoEvento())) {
                logWriter.error("El participante " + inscripcion.getCorreo() + " no está inscrito en el evento: "
                        + inscripcion.getCodigoEvento());
                return false;
            }

            if (!validator.existsPayment(inscripcion.getCorreo(), inscripcion.getCodigoEvento())) {
                logWriter.error(
                        "El participante " + inscripcion.getCorreo() + " no ha realizado el pago para el evento: "
                                + inscripcion.getCodigoEvento());
                return false;

            }

            if (validator.existsValidateRegistration(inscripcion)) {
                logWriter.error("La validación de inscripción ya existe: " + inscripcion.getCorreo() + " - "
                        + inscripcion.getCodigoEvento());
                return false;

            }

            if (control.insert(inscripcion, conn) != null) {
                validator.addValidarInscripcion(inscripcion);
                logWriter.info("Validación de inscripción registrada: " + inscripcion);
                return true;
            } else {
                logWriter.error("Error al insertar la validación de inscripción en la base de datos.");
                return false;
            }

        } catch (Exception e) {
            try {
                logWriter.error("Excepción procesando VALIDAR_INSCRIPCION: " + e.getMessage());
            } catch (Exception ignore) {
            }
            return false;
        }
    }

    /**
     * Inserta una validación de inscripción directamente desde un formulario.
     * 
     * Este método se utiliza cuando los datos provienen de la interfaz de usuario
     * en lugar de un archivo de procesamiento por lotes.
     * 
     * @param validarInscripcion El modelo de validación de inscripción a insertar
     * @return true si la inserción fue exitosa, false en caso contrario
     */
    public boolean insertFromForm(ValidateRegistrationModel validarInscripcion) {
        try {
            return control.insert(validarInscripcion, conn) != null;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Valida los datos de una validación de inscripción desde un formulario.
     * 
     * Realiza validaciones completas incluyendo la integridad de los datos,
     * verificaciones de negocio y validaciones específicas del controlador.
     * 
     * @param validarInscripcion El modelo de validación de inscripción a validar
     * @return "Ok" si todas las validaciones pasan, mensaje de error específico en
     *         caso contrario
     */
    public String validateForm(ValidateRegistrationModel validarInscripcion) {

        if (validarInscripcion == null) {
            return "Los datos de la inscripción no pueden ser nulos.";
        }
        if (!validateDataIntegrity(validarInscripcion)) {
            return "Datos invalidos";
        }

        String validation = control.validateRegistration(validarInscripcion, conn);
        if (!"Ok".equals(validation)) {
            return validation;
        }

        return "Ok";

    }

    /**
     * Valida la integridad de los datos de una validación de inscripción.
     * 
     * Verifica que los campos cumplan con los formatos y restricciones requeridas,
     * incluyendo el formato del código de evento y la validez del correo
     * electrónico.
     * 
     * @param validarInscripcion El modelo de validación de inscripción a validar
     * @return true si todos los datos son válidos, false en caso contrario
     * 
     * @apiNote Los criterios de validación incluyen:
     *          <ul>
     *          <li>Código de evento con formato EVT-XXXXXXXX (8 dígitos)</li>
     *          <li>Correo electrónico con formato válido</li>
     *          <li>Campos no nulos y no vacíos</li>
     *          </ul>
     */
    public boolean validateDataIntegrity(ValidateRegistrationModel validarInscripcion) {

        boolean codigoValido = validarInscripcion.getCodigoEvento() != null &&
                validarInscripcion.getCodigoEvento().matches("^EVT-\\d{8}$")
                && validarInscripcion.getCodigoEvento() != null &&
                !validarInscripcion.getCodigoEvento().isBlank();

        boolean isValidEmail = Pattern.matches(
                "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", validarInscripcion.getCorreo())
                && validarInscripcion.getCorreo() != null && !validarInscripcion.getCorreo().isBlank();

        return codigoValido && isValidEmail;

    }

}
