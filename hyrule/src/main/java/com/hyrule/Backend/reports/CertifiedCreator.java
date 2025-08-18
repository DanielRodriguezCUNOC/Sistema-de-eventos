package com.hyrule.Backend.reports;

public class CertifiedCreator {

    /**
     * Crea un certificado para un participante en un evento.
     * 
     * @param codigoEvento       el código del evento
     * @param correoParticipante el correo del participante
     * @return un mensaje de éxito o error
     */
    public String createCertificate(String codigoEvento, String correoParticipante) {
        // Aquí se implementaría la lógica para crear el certificado
        // Por ejemplo, llamar al ControlCertified para insertar el certificado en la
        // base de datos

        return "Certificado creado exitosamente para el evento: " + codigoEvento + " y participante: "
                + correoParticipante;
    }
}