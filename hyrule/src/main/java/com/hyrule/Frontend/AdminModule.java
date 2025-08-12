package com.hyrule.Frontend;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.hyrule.Frontend.event.EventRegisterForm;
import com.hyrule.Frontend.loadarchive.UploadArchiveFrame;

public class AdminModule extends JFrame {

    private JDesktopPane desktopPane;
    private JPanel panelFondo;

    public AdminModule() {
        setTitle("Módulo Administrador");
        setSize(1000, 740);
        setResizable(false);
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
        panelFondo();

        setVisible(true);
    }

    private JMenu crearMenu(String titulo) {
        JMenu menu = new JMenu(titulo);
        menu.setFont(new Font("SansSerif", Font.BOLD, 15));
        menu.setBackground(new Color(33, 141, 230));
        menu.setBorder(new EmptyBorder(5, 20, 5, 20));
        menu.setForeground(new Color(255, 255, 255));
        menu.setOpaque(true);

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

        item.setFocusPainted(false);
        item.setBorderPainted(false);

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
        JMenu menuSubirArchivo = crearMenu("Subir Archivo");

        // * Creamos el ítem de menú para registrar eventos */
        JMenuItem itemRegistrarEvento = crearMenuItem("Registrar Evento");
        itemRegistrarEvento.addActionListener(e -> {
            cerrarVentanas();
            mostrarEventRegisterModule();
            menuEventos.setSelected(false);
            menuBar.repaint();
        });
        menuEventos.add(itemRegistrarEvento);

        JMenuItem itemSubirArchivo = crearMenuItem("Subir Archivo");
        itemSubirArchivo.addActionListener(e -> {
            cerrarVentanas();
            mostrarUploadArchiveModule();
            menuSubirArchivo.setSelected(false);
            menuBar.repaint();
        });
        menuSubirArchivo.add(itemSubirArchivo);

        menuBar.add(menuEventos);
        menuBar.add(Box.createHorizontalStrut(10));
        menuBar.add(menuSubirArchivo);
        return menuBar;
    }

    public void mostrarEventRegisterModule() {
        EventRegisterForm registerModule = new EventRegisterForm(this);
        desktopPane.add(registerModule);
        centrarInternalFrame(registerModule);
        registerModule.setVisible(true);
    }

    public void mostrarUploadArchiveModule() {
        UploadArchiveFrame uploadModule = new UploadArchiveFrame(this);
        desktopPane.add(uploadModule);
        centrarInternalFrame(uploadModule);
        uploadModule.setVisible(true);
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

    private void panelFondo() {
        InputStream imgStream = getClass().getResourceAsStream("/com/hyrule/resources/images/fondo.png");
        panelFondo = new JPanel(new BorderLayout());
        panelFondo.setBackground(new Color(245, 247, 250));

        if (imgStream == null) {
            System.err.println("No se pudo cargar la imagen de fondo.");
            return;

        }
        try {
            Image originalImage = ImageIO.read(imgStream);
            panelFondo.setOpaque(false);
            JLabel lblImage = new JLabel() {
                @Override
                protected void paintComponent(Graphics g) {
                    int altura = desktopPane.getHeight();
                    int ancho = desktopPane.getWidth();
                    double imgRatio = (double) originalImage.getWidth(null) / originalImage.getHeight(null);
                    int nuevaAnchura = ancho;
                    int nuevaAltura = (int) (nuevaAnchura / imgRatio);

                    if (nuevaAltura > altura) {
                        nuevaAltura = altura;
                        nuevaAnchura = (int) (nuevaAltura * imgRatio);

                    }
                    int x = (ancho - nuevaAnchura) / 2;

                    g.drawImage(originalImage, x, 0, nuevaAnchura, nuevaAltura, this);
                }
            };
            panelFondo.add(lblImage, BorderLayout.CENTER);
            panelFondo.setBounds(0, 0, desktopPane.getWidth(), desktopPane.getHeight());
            desktopPane.add(panelFondo);

            desktopPane.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    panelFondo.setSize(desktopPane.getSize());
                    lblImage.repaint();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
