package com.hyrule.Frontend;

import java.awt.*;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.hyrule.Frontend.event.EventRegisterForm;
import com.hyrule.Frontend.loadarchive.UploadArchiveFrame;
import com.hyrule.Frontend.participant.ParticipantRegisterForm;

public class AdminModule extends JFrame {

    private JDesktopPane desktopPane;
    private JPanel sidebar;

    public AdminModule() {
        setTitle("Módulo Administrador - Dashboard");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        sidebar = crearSidebar();
        add(sidebar, BorderLayout.WEST);

        desktopPane = new JDesktopPane();
        desktopPane.setBackground(new Color(240, 242, 245));
        add(desktopPane, BorderLayout.CENTER);
        setVisible(true);
        mostrarVistaInicio();
    }

    private JPanel crearSidebar() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(33, 37, 41));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(200, getHeight()));

        JLabel titulo = new JLabel("ADMIN");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 20));
        titulo.setForeground(Color.WHITE);
        titulo.setBorder(new EmptyBorder(20, 20, 20, 20));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titulo);

        panel.add(Box.createVerticalStrut(20));
        panel.add(crearBotonMenu("Inicio", () -> mostrarVistaInicio()));
        panel.add(Box.createVerticalStrut(10));
        panel.add(crearBotonMenu("Eventos", () -> mostrarEventRegisterModule()));
        panel.add(Box.createVerticalStrut(10));
        panel.add(crearBotonMenu("Subir Archivo", () -> mostrarUploadArchiveModule()));
        panel.add(Box.createVerticalStrut(10));
        panel.add(crearBotonMenu("Participantes", () -> mostrarParticipantRegisterModule()));
        panel.add(Box.createVerticalGlue());
        panel.add(crearBotonMenu("Salir", () -> System.exit(0)));

        return panel;
    }

    private JButton crearBotonMenu(String texto, Runnable accion) {
        JButton btn = new JButton(texto);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(180, 100));
        if (texto.equals("Salir")) {
            btn.setBackground(new Color(220, 53, 69));
        } else {
            btn.setBackground(new Color(52, 58, 64));
            btn.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    btn.setBackground(new Color(73, 80, 87));
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    btn.setBackground(new Color(52, 58, 64));
                }
            });
        }
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 16));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> accion.run());
        btn.setMargin(new Insets(5, 15, 5, 5));

        return btn;
    }

    private void mostrarVistaInicio() {
        cerrarVentanas();

        try (InputStream imgStream = getClass().getResourceAsStream("/com/hyrule/resources/images/fondo.png")) {
            if (imgStream != null) {
                Image imagen = ImageIO.read(imgStream);
                JPanel panelInicio = new JPanel() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        g.drawImage(imagen, 0, 0, getWidth(), getHeight(), this);
                    }
                };
                panelInicio.setBackground(new Color(240, 242, 245));
                panelInicio.setBounds(0, 0, desktopPane.getWidth(), desktopPane.getHeight());
                desktopPane.add(panelInicio);
            } else {
                System.err.println("No se pudo cargar la imagen de fondo.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        desktopPane.repaint();
        desktopPane.revalidate();
    }

    public void mostrarEventRegisterModule() {
        cerrarVentanas();
        EventRegisterForm registerModule = new EventRegisterForm(this);
        desktopPane.add(registerModule);
        centrarInternalFrame(registerModule);
        registerModule.setVisible(true);
    }

    public void mostrarUploadArchiveModule() {
        cerrarVentanas();
        UploadArchiveFrame uploadModule = new UploadArchiveFrame(this);
        desktopPane.add(uploadModule);
        centrarInternalFrame(uploadModule);
        uploadModule.setVisible(true);
    }

    public void mostrarParticipantRegisterModule() {
        cerrarVentanas();
        ParticipantRegisterForm participantModule = new ParticipantRegisterForm(this);
        desktopPane.add(participantModule);
        centrarInternalFrame(participantModule);
        participantModule.setVisible(true);
    }

    public void cerrarVentanas() {
        for (JInternalFrame frame : desktopPane.getAllFrames()) {
            frame.dispose();
        }
        // desktopPane.removeAll();
        desktopPane.repaint();
    }

    private void centrarInternalFrame(JInternalFrame frame) {
        Dimension desktopSize = desktopPane.getSize();
        Dimension jInternalFrameSize = frame.getSize();
        frame.setLocation((desktopSize.width - jInternalFrameSize.width) / 2,
                (desktopSize.height - jInternalFrameSize.height) / 2);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AdminModule::new);
    }
}
