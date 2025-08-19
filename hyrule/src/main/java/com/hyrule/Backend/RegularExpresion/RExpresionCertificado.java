package com.hyrule.Backend.RegularExpresion;

import java.util.regex.Pattern;

import com.hyrule.Backend.model.certified.CertifiedModel;

/**
 * Clase para parsear líneas de certificados usando expresiones regulares.
 * Extrae y valida datos de certificados desde texto en formato específico.
 */
public class RExpresionCertificado {

    private static final Pattern CODIGO_EVENTO = Pattern.compile("^EVT-\\d{8}$");
    private static final Pattern EMAIL = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");

    public CertifiedModel parseCertified(String linea) {
        // Verificación básica del formato
        if (linea == null || !linea.startsWith("CERTIFICADO(") || !linea.endsWith(");")) {
            return null;
        }

        try {
            // Extraer contenido dentro de paréntesis de forma más robusta
            String contenido = linea.substring("CERTIFICADO".length()).trim();
            contenido = contenido.substring(1, contenido.length() - 2).trim(); // Remover () y ;

            // Dividir argumentos respetando comillas
            String[] partes = splitArgs(contenido);

            if (partes.length != 2) {
                return null;
            }

            // Limpiar y validar cada campo
            String correo = cleanQuotes(partes[0]);
            String codigoEvento = cleanQuotes(partes[1]);

            if (!CODIGO_EVENTO.matcher(codigoEvento).matches()) {

                return null;
            }
            if (!EMAIL.matcher(correo).matches()) {

                return null;
            }

            return new CertifiedModel(codigoEvento, correo);

        } catch (Exception e) {

            return null;
        }
    }

    private String cleanQuotes(String str) {
        return str.replaceAll("^\"|\"$", "").trim();
    }

    private String[] splitArgs(String input) {
        return input.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
    }
}