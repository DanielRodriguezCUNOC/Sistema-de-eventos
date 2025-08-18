package com.hyrule.Backend.Validation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import com.hyrule.Backend.model.activity.ActivityModel;
import com.hyrule.Backend.model.asistencia.AttendanceModel;
import com.hyrule.Backend.model.certified.CertifiedModel;
import com.hyrule.Backend.model.event.EventModel;
import com.hyrule.Backend.model.inscripcion.RegistrationModel;
import com.hyrule.Backend.model.inscripcion.RegistrationType;
import com.hyrule.Backend.model.participant.ParticipantModel;
import com.hyrule.Backend.model.payment.PaymentModel;
import com.hyrule.Backend.model.validate_registration.ValidateRegistrationModel;

/**
 * Archivo de validación centralizado para el sistema de gestión de eventos.
 * 
 * Esta clase implementa el patrón Singleton y actúa como un repositorio en
 * memoria
 * para almacenar y validar la integridad de los datos durante el procesamiento
 * de archivos.
 * Mantiene estructuras de datos para eventos, actividades, participantes,
 * inscripciones,
 * certificados, asistencias, pagos y validaciones de inscripción.
 * 
 * <p>
 * Funcionalidades principales:
 * </p>
 * <ul>
 * <li>Validación de duplicados en tiempo real durante el procesamiento</li>
 * <li>Verificación de integridad referencial entre entidades</li>
 * <li>Almacenamiento temporal para validaciones cruzadas</li>
 * <li>Prevención de inconsistencias en los datos</li>
 * </ul>
 * 
 * @author Sistema de Eventos Hyrule
 * @version 1.0
 * @since 2025
 * 
 * @see EventModel
 * @see ParticipantModel
 * @see ActivityModel
 * @see RegistrationModel
 */
public class ValidationArchive {

    /**
     * Instancia única del patrón Singleton.
     */
    private static ValidationArchive instance;

    /**
     * Estructura para almacenar eventos durante la validación de archivos.
     */
    private final Set<EventModel> EVENTOS = new HashSet<>();

    /**
     * Estructura para almacenar actividades durante la validación de archivos.
     */
    private final Set<ActivityModel> ACTIVIDADES = new HashSet<>();

    /**
     * Estructura para almacenar participantes durante la validación de archivos.
     */
    private final Set<ParticipantModel> PARTICIPANTE = new HashSet<>();

    /**
     * Estructura para almacenar inscripciones durante la validación de archivos.
     */
    private final Set<RegistrationModel> INSCRIPCION = new HashSet<>();

    /**
     * Estructura para almacenar certificados durante la validación de archivos.
     */
    private final Set<CertifiedModel> CERTIFICADO = new HashSet<>();

    /**
     * Estructura para almacenar asistencias durante la validación de archivos.
     */
    private final Set<AttendanceModel> ASISTENCIA = new HashSet<>();

    /**
     * Estructura para almacenar pagos durante la validación de archivos.
     */
    private final Set<PaymentModel> PAGO = new HashSet<>();

    /**
     * Estructura para almacenar validaciones de inscripción durante el
     * procesamiento de archivos.
     */
    private final Set<ValidateRegistrationModel> VALIDAR_INSCRIPCION = new HashSet<>();

    /**
     * Constructor privado para implementar el patrón Singleton.
     */
    private ValidationArchive() {

    }

    /**
     * Obtiene la instancia única de ValidationArchive (patrón Singleton).
     * 
     * @return La instancia única de ValidationArchive
     * @synchronized Garantiza thread-safety en la creación de la instancia
     */
    public static synchronized ValidationArchive getInstance() {
        if (instance == null) {
            instance = new ValidationArchive();
        }
        return instance;
    }

    // ========== Funcionalidades para Eventos ==========

    /**
     * Agrega un evento a la estructura de validación temporal.
     * 
     * @param evento El modelo de evento a agregar
     * @throws NullPointerException si el evento es null
     */
    public void addEvento(EventModel evento) {
        EVENTOS.add(evento);
    }

    /**
     * Verifica si un evento ya existe en la estructura de validación.
     * 
     * @param eventCode El código del evento a verificar
     * @return true si el evento existe, false en caso contrario
     * @throws IllegalArgumentException si ocurre un error durante la verificación
     */
    public boolean existsEvent(String eventCode) {
        try {
            for (EventModel event : EVENTOS) {
                return event.getCodigoEvento().equals(eventCode);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Error al verificar la existencia del evento: " + e.getMessage());
        }
        return false;
    }

    /**
     * Verifica si existe un evento con el mismo título y fecha.
     * 
     * Esta validación previene la duplicación de eventos que podrían generar
     * confusión o conflictos en el sistema.
     * 
     * @param title El título del evento a verificar
     * @param date  La fecha del evento a verificar
     * @return true si existe un evento con el mismo título y fecha, false en caso
     *         contrario
     * @throws IllegalArgumentException si ocurre un error durante la verificación
     */
    public boolean eventHaveSameTitleAndDate(String title, LocalDate date) {

        try {
            for (EventModel event : EVENTOS) {
                return event.getTituloEvento().equalsIgnoreCase(title) && event.getFechaEvento().equals(date);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Error al verificar el evento: " + e.getMessage());
        }
        return false;

    }

    // ========== Funcionalidades para Actividades ==========

    /**
     * Agrega una actividad a la estructura de validación temporal.
     * 
     * @param actividad El modelo de actividad a agregar
     * @throws NullPointerException si la actividad es null
     */
    public void addActividad(ActivityModel actividad) {
        ACTIVIDADES.add(actividad);
    }

    /**
     * Verifica si una actividad ya existe en la estructura de validación.
     * 
     * @param activityCode El código de la actividad a verificar
     * @return true si la actividad existe, false en caso contrario
     * @throws IllegalArgumentException si ocurre un error durante la verificación
     */
    public boolean existsActivity(String activityCode) {
        try {
            for (ActivityModel activity : ACTIVIDADES) {
                return activity.getCodigoActividad().equals(activityCode);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Error al verificar la existencia de la actividad: " + e.getMessage());
        }
        return false;
    }

    /**
     * Verifica si un participante está inscrito como ASISTENTE en algún evento.
     * 
     * Esta validación es importante para determinar los permisos y capacidades
     * del participante dentro del sistema de eventos.
     * 
     * @param emailParticipante El correo electrónico del participante a verificar
     * @return true si el participante está inscrito como ASISTENTE, false en caso
     *         contrario
     * @throws IllegalArgumentException si ocurre un error durante la verificación
     */
    public boolean isAsistente(String emailParticipante) {
        try {
            for (RegistrationModel register : INSCRIPCION) {
                if (register.getCorreoParticipante().equals(emailParticipante)) {
                    if (register.getTipoInscripcion().equals(RegistrationType.ASISTENTE)) {
                        return register.getTipoInscripcion().equals(RegistrationType.ASISTENTE);
                    }

                }
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Error al verificar el tipo de participante: " + e.getMessage());
        }
        return false;
    }

    // ========== Funcionalidades para Participantes ==========

    /**
     * Agrega un participante a la estructura de validación temporal.
     * 
     * @param participante El modelo de participante a agregar
     * @throws NullPointerException si el participante es null
     */
    public void addParticipante(ParticipantModel participante) {
        PARTICIPANTE.add(participante);
    }

    /**
     * Verifica si un participante ya existe en la estructura de validación.
     * 
     * @param participantEmail El correo electrónico del participante a verificar
     * @return true si el participante existe, false en caso contrario
     * @throws IllegalArgumentException si ocurre un error durante la verificación
     */
    public boolean existsParticipant(String participantEmail) {
        try {
            for (ParticipantModel participant : PARTICIPANTE) {
                return participant.getCorreo_participante().equals(participantEmail);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Error al verificar la existencia del participante: " + e.getMessage());
        }
        return false;
    }

    // ========== Funcionalidades para Inscripciones ==========

    /**
     * Agrega una inscripción a la estructura de validación temporal.
     * 
     * @param inscripcion El modelo de inscripción a agregar
     * @throws NullPointerException si la inscripción es null
     */
    public void addInscripcion(RegistrationModel inscripcion) {
        INSCRIPCION.add(inscripcion);
    }

    /**
     * Verifica si una inscripción ya existe en la estructura de validación.
     * 
     * Valida la combinación única de código de evento y correo del participante
     * para prevenir inscripciones duplicadas.
     * 
     * @param codeEvent        El código del evento
     * @param participantEmail El correo electrónico del participante
     * @return true si la inscripción existe, false en caso contrario
     * @throws IllegalArgumentException si ocurre un error durante la verificación
     */
    public boolean existsRegistration(String codeEvent, String participantEmail) {
        try {
            for (RegistrationModel registration : INSCRIPCION) {
                return registration.getCodigoEvento().equals(codeEvent)
                        && registration.getCorreoParticipante().equals(participantEmail);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Error al verificar la existencia de la inscripción: " + e
                    .getMessage());
        }
        return false;
    }

    // ========== Funcionalidades para Certificados ==========

    /**
     * Agrega un certificado a la estructura de validación temporal.
     * 
     * @param certificado El modelo de certificado a agregar
     * @throws NullPointerException si el certificado es null
     */
    public void addCertificado(CertifiedModel certificado) {
        CERTIFICADO.add(certificado);
    }

    /**
     * Verifica si un certificado ya existe en la estructura de validación.
     * 
     * Valida la combinación única de código de evento y correo del participante
     * para prevenir emisión duplicada de certificados.
     * 
     * @param codeEvent        El código del evento
     * @param participantEmail El correo electrónico del participante
     * @return true si el certificado existe, false en caso contrario
     * @throws IllegalArgumentException si ocurre un error durante la verificación
     */
    public boolean existsCertificado(String codeEvent, String participantEmail) {
        try {
            for (CertifiedModel certificado : CERTIFICADO) {
                return certificado.getCodigoEvento().equals(codeEvent)
                        && certificado.getCorreoParticipante().equals(participantEmail);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Error al verificar la existencia del certificado: " + e.getMessage());
        }
        return false;
    }

    // ========== Funcionalidades para Asistencia ==========

    /**
     * Agrega un registro de asistencia a la estructura de validación temporal.
     * 
     * @param asistencia El modelo de asistencia a agregar
     * @throws NullPointerException si la asistencia es null
     */
    public void addAsistencia(AttendanceModel asistencia) {
        ASISTENCIA.add(asistencia);
    }

    /**
     * Verifica si existe un registro de asistencia para un participante en una
     * actividad específica.
     * 
     * Esta validación es crucial para prevenir registros duplicados de asistencia
     * y para verificar la participación efectiva en las actividades.
     * 
     * @param emailParticipante El correo electrónico del participante
     * @param activityCode      El código de la actividad
     * @return true si existe el registro de asistencia, false en caso contrario
     * @throws IllegalArgumentException si ocurre un error durante la verificación
     */
    public boolean existsAttendance(String emailParticipante, String activityCode) {
        try {
            for (AttendanceModel asistencia : ASISTENCIA) {
                return asistencia.getCorreoParticipante().equals(emailParticipante)
                        && asistencia.getCodigoActividad().equals(activityCode);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Error al verificar la asistencia del participante: " + e.getMessage());
        }
        return false;

    }

    // ========== Funcionalidades para Pagos ==========

    /**
     * Agrega un registro de pago a la estructura de validación temporal.
     * 
     * @param pago El modelo de pago a agregar
     * @throws NullPointerException si el pago es null
     */
    public void addPago(PaymentModel pago) {
        PAGO.add(pago);
    }

    /**
     * Verifica si un participante ya ha realizado un pago para un evento
     * específico.
     * 
     * Esta validación previene pagos duplicados y asegura la integridad
     * del sistema de facturación.
     * 
     * @param emailParticipante El correo electrónico del participante
     * @param eventCode         El código del evento
     * @return true si existe el registro de pago, false en caso contrario
     * @throws IllegalArgumentException si ocurre un error durante la verificación
     */
    public boolean existsPayment(String emailParticipante, String eventCode) {
        try {
            for (PaymentModel pago : PAGO) {
                return pago.getCorreo().equals(emailParticipante)
                        && pago.getCodigoEvento().equals(eventCode);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Error al verificar el pago del participante: " + e.getMessage());
        }
        return false;
    }

    /**
     * Valida si el monto pagado coincide con el costo del evento.
     * 
     * Esta validación asegura que los participantes hayan pagado el monto
     * correcto según el costo establecido para el evento.
     * 
     * @param montoPagado El monto que fue pagado por el participante
     * @param eventCode   El código del evento para verificar su costo
     * @return true si el monto pagado coincide con el costo del evento, false en
     *         caso contrario
     * @throws IllegalArgumentException si ocurre un error durante la verificación
     * 
     * @apiNote Utiliza {@link BigDecimal#compareTo(BigDecimal)} para comparación
     *          precisa de montos
     */
    public boolean validarMontoPago(BigDecimal montoPagado, String eventCode) {
        try {
            for (EventModel event : EVENTOS) {
                if (event.getCodigoEvento().equals(eventCode)) {
                    return event.getCostoEvento().compareTo(montoPagado) == 0;
                }

            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Error al verificar el monto del pago: " + e.getMessage());
        }
        return false;

    }

    // ========== Funcionalidades para Validación de Inscripciones ==========

    /**
     * Agrega una validación de inscripción a la estructura de validación temporal.
     * 
     * @param validarInscripcion El modelo de validación de inscripción a agregar
     * @throws NullPointerException si la validación de inscripción es null
     */
    public void addValidarInscripcion(ValidateRegistrationModel validarInscripcion) {
        VALIDAR_INSCRIPCION.add(validarInscripcion);
    }

    /**
     * Verifica si una validación de inscripción ya existe en la estructura de
     * validación.
     * 
     * Esta validación previene el procesamiento duplicado de validaciones de
     * inscripción
     * para la misma combinación de participante y evento.
     * 
     * @param validarInscripcion El modelo de validación de inscripción a verificar
     * @return true si la validación ya existe, false en caso contrario
     * @throws IllegalArgumentException si ocurre un error durante la verificación
     * 
     * @apiNote Compara por código de evento y correo del participante para
     *          determinar unicidad
     */
    public boolean existsValidateRegistration(ValidateRegistrationModel validarInscripcion) {
        try {
            for (ValidateRegistrationModel validate : VALIDAR_INSCRIPCION) {
                return validate.getCodigoEvento().equals(validarInscripcion.getCodigoEvento())
                        && validate.getCorreo().equals(validarInscripcion.getCorreo());
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Error al verificar la existencia de la validación de inscripción: " + e.getMessage());
        }
        return false;
    }
}
