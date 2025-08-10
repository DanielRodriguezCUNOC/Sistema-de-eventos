package com.hyrule.Frontend;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.hyrule.Frontend.event.EventRegisterForm;

public class AdminModule extends JFrame {

    private JDesktopPane desktopPane;
    private boolean archivoLeido = false;
    private JPanel panelLoadArchive;

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

        archivoLeido();

        setVisible(true);
    }

    // *Verificamos si el archivo ya fue leído */
    private void archivoLeido() {
        panelLoadArchive = new JPanel();
        panelLoadArchive.setLayout(new BorderLayout());
        panelLoadArchive.setBackground(new Color(240, 242, 245));
        pedirArchivo();
        archivoLeido = true;
        desktopPane.add(panelLoadArchive);

        if (archivoLeido) {
            setContentPane(desktopPane);

            setJMenuBar(crearMenuBar());
        } else {

            System.out.println("Archivo no leído.");
        }
    }

    private JMenu crearMenu(String titulo) {
        JMenu menu = new JMenu(titulo);
        menu.setFont(new Font("SansSerif", Font.BOLD, 15));
        menu.setBackground(new Color(33, 141, 230));
        menu.setBorder(new EmptyBorder(5, 20, 5, 20));
        menu.setForeground(new Color(255, 255, 255));
        menu.setOpaque(true);

        // Efectos hover y evitar que el menú mantenga el estilo de selección
        menu.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                menu.setBackground(new Color(33, 141, 230));
                menu.setCursor(new Cursor(Cursor.HAND_CURSOR));
                menu.repaint();
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                menu.setBackground(new Color(33, 141, 230));
                menu.setSelected(false);
                menu.repaint();
            }
        });

        return menu;
    }

    private JMenuItem crearMenuItem(String texto) {
        JMenuItem item = new JMenuItem(texto);
        item.setFont(new Font("SansSerif", Font.PLAIN, 14));
        item.setBackground(new Color(33, 141, 230));
        item.setBorder(new EmptyBorder(8, 20, 8, 20));
        item.setForeground(new Color(255, 255, 255));
        item.setOpaque(true);

        // Deshabilitar el foco visual por defecto
        item.setFocusPainted(false);
        item.setBorderPainted(false);

        // Efectos hover mejorados
        item.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                item.setBackground(new Color(33, 141, 230));
                item.setForeground(Color.WHITE);
                item.setCursor(new Cursor(Cursor.HAND_CURSOR));
                item.setSelected(false);
                item.repaint();
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                item.setBackground(new Color(33, 141, 230));
                item.setForeground(Color.WHITE);
                item.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                item.setSelected(false);
                item.repaint();
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

            // * Quitar la selección del menú después de hacer clic */
            menuEventos.setSelected(false);
            menuBar.repaint();
        });
        menuEventos.add(itemRegistrarEvento);

        menuBar.add(menuEventos);
        return menuBar;
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

    // *Metodo para pedir al usuario que suba un archivo */
    public void pedirArchivo() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Seleccionar archivo");
        int userSelection = fileChooser.showOpenDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            // Aquí puedes manejar el archivo seleccionado
            System.out.println("Archivo seleccionado: " + fileChooser.getSelectedFile().getAbsolutePath());
        } else {
            System.out.println("No se seleccionó ningún archivo.");
        }
    }

}
