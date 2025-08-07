package com.hyrule.Frontend;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.hyrule.Frontend.event.EventModule;
import com.hyrule.Frontend.event.EventRegisterForm;

public class AdminModule extends JFrame {

    private JDesktopPane desktopPane;

    public AdminModule() {
        setTitle("Módulo Administrador");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        desktopPane = new JDesktopPane() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                setBackground(new Color(240, 242, 245));
            }
        };

        setContentPane(desktopPane);

        setJMenuBar(crearMenuBar());

        setVisible(true);
    }

    private JMenu crearMenu(String titulo) {
        JMenu menu = new JMenu(titulo);
        menu.setFont(new Font("SansSerif", Font.BOLD, 15));
        menu.setForeground(Color.WHITE);
        menu.setOpaque(false);
        menu.setBorder(null);
        return menu;
    }

    private JMenuItem crearMenuItem(String texto) {
        JMenuItem item = new JMenuItem(texto);
        item.setFont(new Font("SansSerif", Font.PLAIN, 14));
        item.setBackground(Color.WHITE);
        item.setBorder(new EmptyBorder(5, 20, 5, 20));
        item.setForeground(new Color(33, 37, 41));

        // *Creamos un efecto hover al pasar el mouse */
        item.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                item.setBackground(new Color(220, 220, 220));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                item.setBackground(Color.WHITE);
            }
        });

        return item;
    }

    private JMenuBar crearMenuBar() {
        // *Creamos la barra de menú */
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(33, 37, 41));
        menuBar.setBorder(new EmptyBorder(5, 10, 5, 10));

        // *Creamos el menú de Eventos */
        JMenu menuEventos = crearMenu("Eventos");

        // * Creamos el ítem de menú para registrar eventos */
        JMenuItem itemRegistrarEvento = crearMenuItem("Registrar Evento");
        itemRegistrarEvento.addActionListener(e -> {
            cerrarVentanas();
            mostrarEventRegisterModule();
        });
        menuEventos.add(itemRegistrarEvento);

        menuBar.add(menuEventos);
        return menuBar;
    }

    public void mostrarEventModule() {
        EventModule eventModule = new EventModule(this);
        desktopPane.add(eventModule);
        centrarInternalFrame(eventModule);
        eventModule.setVisible(true);
    }

    public void mostrarEventRegisterModule() {
        EventRegisterForm registerModule = new EventRegisterForm(this);
        desktopPane.add(registerModule);
        centrarInternalFrame(registerModule);
        registerModule.setVisible(true);
    }

    public void cerrarVentanas() {
        for (JInternalFrame frame : desktopPane.getAllFrames()) {
            frame.dispose();
        }
    }

    private void centrarInternalFrame(JInternalFrame frame) {
        Dimension desktopSize = desktopPane.getSize();
        Dimension jInternalFrameSize = frame.getSize();
        frame.setLocation((desktopSize.width - jInternalFrameSize.width) / 2,
                (desktopSize.height - jInternalFrameSize.height) / 2);
    }

}
