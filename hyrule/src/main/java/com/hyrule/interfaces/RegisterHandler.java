package com.hyrule.interfaces;

import com.hyrule.Backend.LogFormatter;

/**
 * Interfaz para el procesamiento de registros de datos.
 * Define el contrato para procesar líneas de texto y generar logs de errores.
 */
public interface RegisterHandler {

    /**
     * Procesa una línea de texto y registra errores si es necesario.
     * 
     * @param linea     la línea de texto a procesar
     * @param logWriter el escritor para registrar errores
     * @return true si el procesamiento fue exitoso, false en caso contrario
     */
    boolean process(String linea, LogFormatter logWriter);

}
