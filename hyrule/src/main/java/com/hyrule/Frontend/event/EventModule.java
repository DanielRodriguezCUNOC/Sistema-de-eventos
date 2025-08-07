package com.hyrule.Frontend.event;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JInternalFrame;

import com.hyrule.Frontend.AdminModule;

public class EventModule extends JInternalFrame {

    public EventModule() {
        this(null);
    }

    public EventModule(AdminModule adminView) {
        super("Eventos", true, true, true, true);
        setSize(800, 600);
        setLayout(new BorderLayout());

        JButton btnIrARegistro = new JButton("Ir a Registro de Eventos");
        btnIrARegistro.addActionListener(e -> {
            adminView.mostrarEventRegisterModule();
        });
        add(btnIrARegistro, BorderLayout.CENTER);
    }

}
