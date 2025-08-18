package com.hyrule.Backend.handler;

import java.io.BufferedWriter;
import java.time.format.DateTimeFormatter;
import com.hyrule.Backend.RegularExpresion.RExpresionEvento;
import com.hyrule.Backend.Validation.ValidationArchive;
import com.hyrule.Backend.model.event.EventModel;
import com.hyrule.Backend.persistence.event.ControlEvent;
import com.hyrule.interfaces.RegisterHandler;

/**
 * Manejador para el registro de eventos desde archivos y formularios.
 * Procesa validaciones, duplicados y persistencia en base de datos.
 */
public class EventRegisterHandler implements RegisterHandler {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /** Controlador de persistencia para operaciones de eventos */
    private final ControlEvent control = new ControlEvent();

    /** Validador singleton para verificar duplicados */
    ValidationArchive validator = ValidationArchive.getInstance();

    /**
     * Procesa una línea de evento desde archivo de carga masiva.
     * 
     * @param linea     línea del archivo con datos del evento
     * @param logWriter escritor para registrar errores y éxitos
     * @return true si el evento se procesó correctamente
     */
    @Override
    public boolean process(String linea, BufferedWriter logWriter) {
        try {

            // *Validamos la linea con la expresion regular */
            RExpresionEvento parser = new RExpresionEvento();
            EventModel event = parser.parseEvent(linea.trim());

            if (event == null) {
                logWriter.write("Línea inválida o incompleta: " + linea);
                logWriter.newLine();
                return false;
            }
            if (validator.existsEvent(event.getCodigoEvento())) {
                logWriter.write("El evento ya existe: " + event.getCodigoEvento());
                logWriter.newLine();
                return false;

            }
            if (validator.eventHaveSameTitleAndDate(event.getTituloEvento(), event.getFechaEvento())) {
                logWriter.write("Ya existe un evento con el mismo título y fecha: " + event.getTituloEvento() + " - "
                        + event.getFechaEvento().format(DATE_FORMAT));
                logWriter.newLine();
                return false;
            }

            // *Agregamos el evento a la estructura de validacion */
            validator.addEvento(event);

            logWriter.write("Evento registrado: " + event);
            logWriter.newLine();

            // * Insertamos el evento en la base de datos */
            return control.insert(event) != null;

        } catch (Exception e) {
            try {
                logWriter.write("Excepción procesando REGISTRO_EVENTO: " + e.getMessage());
                logWriter.newLine();
            } catch (Exception ignore) {
            }
            return false;
        }
    }

    /**
     * Inserta un evento directamente desde formulario.
     * 
     * @param event el evento a insertar
     * @return true si se insertó correctamente
     */
    public boolean insertFromForm(EventModel event) {
        return control.insert(event) != null;
    }

    /**
     * Valida los datos de un evento desde formulario.
     * 
     * @param event    el evento a validar
     * @param fechaStr la fecha en formato string
     * @param costoStr el costo en formato string
     * @return "Ok" si es válido, mensaje de error si no
     */
    public String validateForm(EventModel event, String fechaStr, String costoStr) {
        if (event == null) {
            return "El evento no puede ser nulo.";
        }
        if (!validateDataIntegrity(event, fechaStr, costoStr)) {
            return "Datos del evento inválidos.";
        }

        String validation = control.validateEvent(event);
        if (!"Ok".equals(validation)) {
            return validation;

        }

        return "Ok";

    }

    /**
     * Valida la integridad de los datos del evento usando expresiones regulares.
     * 
     * @param evento   el evento a validar
     * @param fechaStr la fecha en formato string
     * @param costoStr el costo en formato string
     * @return true si todos los datos son válidos
     */
    private boolean validateDataIntegrity(EventModel evento, String fechaStr, String costoStr) {

        boolean codigoValido = evento.getCodigoEvento() != null &&
                evento.getCodigoEvento().matches("^EVT-\\d{8}$");

        boolean fechaValida = fechaStr != null && fechaStr.matches(
                "^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[0-2])/([0-9]{4})$");

        boolean tipoValido = evento.getTipoEvento() != null &&
                evento.getTipoEvento().name().matches("^(CHARLA|CONGRESO|TALLER|DEBATE)$");

        boolean tituloValido = evento.getTituloEvento() != null &&
                evento.getTituloEvento().matches("^[a-zA-ZÁÉÍÓÚáéíóúÑñ0-9\\-\\s]{1,150}$");

        boolean ubicacionValida = evento.getUbicacionEvento() != null &&
                evento.getUbicacionEvento().matches("^[a-zA-ZÁÉÍÓÚáéíóúÑñ0-9.,()\\-\\s]{1,150}$")
                && !evento.getUbicacionEvento().isBlank();

        boolean cupoValido = evento.getCupoMaxParticipantes() != null &&
                evento.getCupoMaxParticipantes() > 0;

        boolean costoValido = costoStr != null &&
                costoStr.matches("^\\d+(\\.\\d{1,2})?$");

        return codigoValido && fechaValida && tipoValido && tituloValido &&
                ubicacionValida && cupoValido && costoValido;
    }

}
