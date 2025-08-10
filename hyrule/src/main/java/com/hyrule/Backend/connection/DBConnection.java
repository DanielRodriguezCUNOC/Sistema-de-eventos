package com.hyrule.Backend.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String IP = "localhost";
    private static final String PORT = "3306";
    private static final String SCHEMA = "hyruledb";
    private static final String USER = "triforce_software";
    private static final String PASSWORD = "Hyrule";

    //*Protocolo para la conexión a la base de datos */

    private static final String URL = "jdbc:mysql://" + IP + ":" + PORT + "/" + SCHEMA;

    //* Método para obtener la conexión a la base de datos */

    private Connection connection;
    public void connect(){
        System.out.println("URL de conexión: " + URL);

        try {
                    connection = DriverManager.getConnection(URL, USER, PASSWORD);
                    //* Obtenemos el esquema, que significa la base de datos a la que estamos conectados */
                    System.out.println("Esquema: " + connection.getSchema());
                    //* El catalogo se refiere a la base de datos en uso */
                    System.out.println("Catalogo: " + connection.getCatalog());

        } catch (SQLException e) {
            System.out.println("Error al conectar a la base de datos");
            e.printStackTrace();
        }

    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Conexión cerrada exitosamente.");
            } catch (SQLException e) {
                System.out.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }

    public Connection getConnection() {
        if (connection == null) {
            connect();
        }
        return connection;
    }
}
