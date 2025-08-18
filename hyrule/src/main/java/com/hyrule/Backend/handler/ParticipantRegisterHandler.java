package com.hyrule.Backend.handler;

import java.io.BufferedWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.regex.Pattern;

import com.hyrule.Backend.RegularExpresion.RExpresionParticipante;
import com.hyrule.Backend.Validation.ValidationArchive;
import com.hyrule.Backend.model.participant.ParticipantModel;
import com.hyrule.Backend.persistence.participant.ControlParticipant;
import com.hyrule.interfaces.RegisterHandler;

/**
 * Manejador para el procesamiento y registro de participantes en el sistema de
 * eventos.
 * 
 * Esta clase se encarga de procesar las líneas de registro de participantes
 * desde archivos,
 * validar la integridad de los datos, verificar duplicados y registrar los
 * participantes
 * tanto en la estructura de validación temporal como en la base de datos.
 * 
 * <p>
 * Funcionalidades principales:
 * </p>
 * <ul>
 * <li>Procesamiento de líneas de archivo con expresiones regulares</li>
 * <li>Validación de integridad de datos de participantes</li>
 * <li>Verificación de duplicados en tiempo real</li>
 * <li>Registro desde formularios de la interfaz de usuario</li>
 * <li>Logging detallado del proceso de registro</li>
 * </ul>
 * 
 * @author Sistema de Eventos Hyrule
 * @version 1.0
 * @since 2025
 * 
 * @see ParticipantModel
 * @see ControlParticipant
 * @see ValidationArchive
 * @see RegisterHandler
 */
public class ParticipantRegisterHandler implements RegisterHandler {

    /**
     * Controlador para las operaciones de persistencia de participantes en la base
     * de datos.
     */
    private ControlParticipant control = new ControlParticipant();

    /**
     * Conexión a la base de datos.
     */
    private Connection conn;

    /**
     * Constructor que recibe la conexión a la base de datos.
     */
    public ParticipantRegisterHandler(Connection conn) {
        this.conn = conn;
    }

    /**
     * Instancia del validador de archivos para verificar duplicados y mantener
     * integridad temporal.
     */
    ValidationArchive validator = ValidationArchive.getInstance();

    /**
     * Procesa una línea de texto que contiene información de registro de
     * participante.
     * 
     * Este método analiza la línea utilizando expresiones regulares, valida que el
     * participante
     * no exista previamente, lo agrega a la estructura de validación temporal y lo
     * registra
     * en la base de datos. Todo el proceso se registra en el log para auditoría.
     * 
     * @param linea     El texto de la línea a procesar que contiene los datos del
     *                  participante
     * @param logWriter El escritor para registrar logs del proceso de registro
     * @return true si el participante se procesó y registró exitosamente, false en
     *         caso contrario
     * 
     * @throws Exception Si ocurre un error durante el procesamiento, se captura y
     *                   registra en el log
     * 
     * @apiNote El formato esperado de la línea debe coincidir con el patrón
     *          definido en RExpresionParticipante
     */
    @Override
    public boolean process(String linea, BufferedWriter logWriter) {
        try {

            // *Validamos con la expresion regular */
            RExpresionParticipante parser = new RExpresionParticipante();
            ParticipantModel participante = parser.parseParticipant(linea.trim());
            if (participante == null) {
                logWriter.write("Línea inválida o incompleta: " + linea);
                logWriter.newLine();
                return false;
            }

            // *Validamos si el participante ya existe */
            if (validator.existsParticipant(participante.getCorreo_participante())) {
                logWriter.write("El participante ya existe: " + participante.getCorreo_participante());
                logWriter.newLine();
                return false;

            }

            // *Agregamos los datos al HashSet */
            validator.addParticipante(participante);

            logWriter.write("Participante registrado: " + participante);
            logWriter.newLine();

            // * Insertamos el participante en la base de datos */
            return control.insert(participante, conn) != null;

        } catch (Exception e) {
            try {
                logWriter.write("Excepción procesando REGISTRO_PARTICIPANTE: " + e.getMessage());
                logWriter.newLine();
            } catch (Exception ignore) {
            }
            return false;
        }
    }

    /**
     * Inserta un participante directamente desde un formulario de la interfaz de
     * usuario.
     * 
     * Este método proporciona una forma directa de registrar participantes sin
     * pasar
     * por el proceso de análisis de archivos, ideal para registros manuales desde
     * la UI.
     * 
     * @param participant El modelo de participante a insertar en la base de datos
     * @return true si la inserción fue exitosa, false en caso contrario
     * 
     * @apiNote Este método no realiza validaciones adicionales, asume que los datos
     *          ya han sido validados previamente por
     *          {@link #validateForm(ParticipantModel)}
     */
    public boolean insertFromForm(ParticipantModel participant) {
        try {
            return control.insert(participant, conn) != null;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Valida completamente los datos de un participante desde un formulario.
     * 
     * Realiza validaciones exhaustivas incluyendo la integridad de los datos,
     * verificaciones de duplicados en la base de datos y validaciones de negocio
     * específicas para participantes.
     * 
     * @param participante El modelo de participante a validar
     * @return "Ok" si todas las validaciones pasan exitosamente,
     *         mensaje de error específico en caso contrario
     * 
     * @apiNote Las validaciones incluyen:
     *          <ul>
     *          <li>Verificación de nulidad del objeto</li>
     *          <li>Integridad de datos (formatos, longitudes, etc.)</li>
     *          <li>Duplicados por correo electrónico y nombre</li>
     *          <li>Validaciones específicas del controlador</li>
     *          </ul>
     */
    public String validateForm(ParticipantModel participante) {

        if (participante == null) {
            return "Participante no puede ser nulo";
        }

        // *Validamos la integridad de los datos */
        if (!validateDataIntegrity(participante)) {
            return "Datos del participante no son válidos";
        }

        // *Validamos que no existan participantes con el mismo correo o nombre */
        String validation = control.validateParticipant(participante, conn);
        if (!"Ok".equals(validation)) {
            return validation;
        }

        return "Ok";
    }

    /**
     * Valida la integridad de los datos de un participante.
     * 
     * Verifica que todos los campos del participante cumplan con los formatos,
     * restricciones de longitud y patrones requeridos por el sistema.
     * 
     * @param participante El modelo de participante a validar
     * @return true si todos los datos son válidos según los criterios establecidos,
     *         false en caso contrario
     * 
     * @apiNote Los criterios de validación incluyen:
     *          <ul>
     *          <li><strong>Correo electrónico:</strong> Formato RFC estándar, no
     *          nulo, no vacío</li>
     *          <li><strong>Nombre completo:</strong> Máximo 45 caracteres, letras,
     *          números, acentos, guiones y espacios</li>
     *          <li><strong>Institución:</strong> Máximo 150 caracteres, incluye
     *          puntuación básica</li>
     *          <li><strong>Tipo participante:</strong> Debe ser ESTUDIANTE,
     *          PROFESIONAL o INVITADO</li>
     *          </ul>
     * 
     * @see Pattern#matches(String, CharSequence) para detalles de validación con
     *      regex
     */
    private boolean validateDataIntegrity(ParticipantModel participante) {

        boolean isValidEmail = Pattern.matches(
                "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", participante.getCorreo_participante())
                && participante.getCorreo_participante() != null && !participante.getCorreo_participante().isBlank();

        boolean isValidName = Pattern.matches(
                "^[a-zA-Z0-9ÁÉÍÓÚáéíóúÑñ\\-\\s]{1,45}$", participante.getNombre_completo())
                && participante.getNombre_completo() != null && !participante.getNombre_completo().isBlank();

        boolean isValidInstitution = Pattern.matches(
                "^[a-zA-Z0-9ÁÉÍÓÚáéíóúÑñ.,:;()\\-\\s]{1,150}$", participante.getInstitucion())
                && participante.getInstitucion() != null && !participante.getInstitucion().isBlank();

        boolean isValidType = Pattern.matches(
                "^(ESTUDIANTE|PROFESIONAL|INVITADO)$", participante.getTipoParticipante().name())
                && participante.getTipoParticipante() != null;

        return isValidEmail && isValidName && isValidInstitution && isValidType;
    }

}
