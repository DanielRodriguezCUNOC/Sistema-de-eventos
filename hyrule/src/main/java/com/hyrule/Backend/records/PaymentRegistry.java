package com.hyrule.Backend.records;

import java.util.HashSet;
import java.util.Set;

public class PaymentRegistry {
    // * Registro de pagos realizados */
    private static final Set<String> pagosRealizados = new HashSet<>();

    public static void registrarPago(String correo, String codigoEvento) {
        pagosRealizados.add(correo + "|" + codigoEvento);
    }

    public static boolean existePago(String correo, String codigoEvento) {
        return pagosRealizados.contains(correo + "|" + codigoEvento);
    }

    public static void limpiar() {
        pagosRealizados.clear();
    }
}
