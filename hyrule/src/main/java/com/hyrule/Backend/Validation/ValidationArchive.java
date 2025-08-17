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

public class ValidationArchive {

    private static ValidationArchive instance;

    // *Estruturas para validacion de archivo */

    private final Set<EventModel> EVENTOS = new HashSet<>();
    private final Set<ActivityModel> ACTIVIDADES = new HashSet<>();
    private final Set<ParticipantModel> PARTICIPANTE = new HashSet<>();
    private final Set<RegistrationModel> INSCRIPCION = new HashSet<>();
    private final Set<CertifiedModel> CERTIFICADO = new HashSet<>();
    private final Set<AttendanceModel> ASISTENCIA = new HashSet<>();
    private final Set<PaymentModel> PAGO = new HashSet<>();
    private final Set<ValidateRegistrationModel> VALIDAR_INSCRIPCION = new HashSet<>();

    private ValidationArchive() {

    }

    public static synchronized ValidationArchive getInstance() {
        if (instance == null) {
            instance = new ValidationArchive();
        }
        return instance;
    }

    // *========== Funcionalidades para Eventos */

    // * Agregar un evento a la estructura de validacion */
    public void addEvento(EventModel evento) {
        EVENTOS.add(evento);
    }

    // * Verificar si un evento ya existe en la estructura de validacion */
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

    // *Verificamos que un evento no tenga el mismo titulo y la misma fecha */
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

    // *=======Funcionalidades para Actividades====*/

    public void addActividad(ActivityModel actividad) {
        ACTIVIDADES.add(actividad);
    }

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

    // *Verificar que el tipo de participante no este inscrito como ASISTENTE */
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

    // *=======Funcionalidades para Participantes======*/

    // * Agregar un participante a la estructura de validacion */
    public void addParticipante(ParticipantModel participante) {
        PARTICIPANTE.add(participante);
    }

    // * Verificar si un participante ya existe en la estructura de validacion */
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

    // *======Funcionalidades para la Inscripcion=======*/

    // * Agregar una inscripcion a la estructura de validacion */
    public void addInscripcion(RegistrationModel inscripcion) {
        INSCRIPCION.add(inscripcion);
    }

    // * Verificar si una inscripcion ya existe en la estructura de validacion */
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

    // *=======Funcionalidades para Certificados=======*/

    // * Agregar un certificado a la estructura de validacion */
    public void addCertificado(CertifiedModel certificado) {
        CERTIFICADO.add(certificado);
    }

    // * Verificar si un certificado ya existe en la estructura de validacion */
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

    // *=======Funcionalidades para Asistencia=======*/
    public void addAsistencia(AttendanceModel asistencia) {
        ASISTENCIA.add(asistencia);
    }

    // *Validar que un participante esté presente en la asistencia */

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

    // *=======Funcionalidades para Pagos=======*/

    public void addPago(PaymentModel pago) {
        PAGO.add(pago);
    }

    // *Validar si un participante ya ha realizado un pago */

    public boolean existsPayment(String emailParticipante) {
        try {
            for (PaymentModel pago : PAGO) {
                return pago.getCorreo().equals(emailParticipante);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Error al verificar el pago del participante: " + e.getMessage());
        }
        return false;
    }

    // *Validar si el monto del pago es el mismo que el costo del evento */
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

    // *======Funcionalidades para la Validación de Inscripciones=======*/

    public void addValidarInscripcion(ValidateRegistrationModel validarInscripcion) {
        VALIDAR_INSCRIPCION.add(validarInscripcion);
    }

    // * Verificar si una validación de inscripción ya existe en la estructura de
    // validación */
    public boolean existsValidateRegistration(String eventCode, String participantEmail) {
        try {
            for (ValidateRegistrationModel validate : VALIDAR_INSCRIPCION) {
                return validate.getCodigoEvento().equals(eventCode)
                        && validate.getCorreo().equals(participantEmail);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Error al verificar la existencia de la validación de inscripción: " + e.getMessage());
        }
        return false;
    }
}
