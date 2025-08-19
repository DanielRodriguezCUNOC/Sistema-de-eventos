package com.hyrule;

import javax.swing.SwingUtilities;

import com.hyrule.Frontend.AdminModule;

/**
 * Clase principal del sistema de gestión de eventos
 */
public class Main {
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            new AdminModule();
        });

    }
}
