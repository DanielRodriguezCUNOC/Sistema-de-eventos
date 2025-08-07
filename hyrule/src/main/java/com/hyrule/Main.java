package com.hyrule;

import javax.swing.SwingUtilities;

import com.hyrule.Frontend.AdminModule;

public class Main {
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            // Iniciar la aplicación
            new AdminModule();
        });

    }
}
