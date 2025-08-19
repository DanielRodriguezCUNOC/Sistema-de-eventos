package com.hyrule.Backend.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase para gestionar la conexión a la base de datos MySQL.
 * Proporciona métodos para establecer, obtener y cerrar conexiones.
 */
public class DBConnection {
    /** Dirección IP del servidor de base de datos */
    private static final String IP = "localhost";

    /** Puerto del servidor MySQL */
    private static final String PORT = "3306";

    /** Nombre del esquema de la base de datos */
    private static final String SCHEMA = "hyruledb";

    /** Usuario para la conexión a la base de datos */
    private static final String USER = "triforce_software";

    /** Contraseña para la conexión a la base de datos */
    /** Contraseña para la conexión a la base de datos */
    private static final String PASSWORD = "Hyrule";

    /** Protocolo para la conexión a la base de datos */
    private static final String URL = "jdbc:mysql://" + IP + ":" + PORT + "/" + SCHEMA;

    /** Instancia de la conexión a la base de datos */
    private Connection connection;

    /**
     * Establece la conexión con la base de datos MySQL.
     * Muestra información del esquema y catálogo una vez conectado.
     */
    public void connect() {

        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);

        } catch (SQLException e) {
            System.out.println("Error al conectar a la base de datos");
            e.printStackTrace();
        }

    }

    /**
     * Cierra la conexión con la base de datos si está activa.
     * Maneja las excepciones que puedan ocurrir durante el cierre.
     */
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.out.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }

    /**
     * Obtiene la conexión a la base de datos.
     * Si no existe una conexión activa, la establece automáticamente.
     * 
     * @return la conexión a la base de datos
     */
    public Connection getConnection() {
        if (connection == null) {
            connect();
        }
        return connection;
    }
}
