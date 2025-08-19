package com.hyrule.Frontend;

import java.awt.*;
import java.io.InputStream;
import java.nio.file.Path;
import java.sql.Connection;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.hyrule.Backend.connection.DBConnection;
import com.hyrule.Frontend.activity.ActivityRegisterForm;
import com.hyrule.Frontend.attendance.AttendanceRegisterForm;
import com.hyrule.Frontend.certified.CertifiedRegisterForm;
import com.hyrule.Frontend.event.EventRegisterForm;
import com.hyrule.Frontend.loadarchive.UploadArchiveFrame;
import com.hyrule.Frontend.participant.ParticipantRegisterForm;
import com.hyrule.Frontend.payment.PaymentRegisterForm;
import com.hyrule.Frontend.registration.InscripcionRegisterForm;
import com.hyrule.Frontend.validate_registration.ValidateRegistrationForm;

public class AdminModule extends JFrame {

    private JDesktopPane desktopPane;
    private JPanel sidebar;
    private Connection connection;
    private DBConnection dbConnection;
    private Path directoryPath;

    public AdminModule() {
        setTitle("Sistema de Administración de Eventos");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        dbConnection = new DBConnection();
        this.connection = dbConnection.getConnection();

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
        panel.add(crearBotonMenu("Subir Archivo", () -> mostrarUploadArchiveModule()));
        panel.add(Box.createVerticalStrut(10));
        panel.add(crearBotonMenu("Eventos", () -> mostrarEventRegisterModule()));
        panel.add(Box.createVerticalStrut(10));
        panel.add(crearBotonMenu("Participantes", () -> mostrarParticipantRegisterModule()));
        panel.add(Box.createVerticalStrut(10));
        panel.add(crearBotonMenu("Actividades", () -> mostrarActivityRegisterModule()));
        panel.add(Box.createVerticalStrut(10));
        panel.add(crearBotonMenu("Inscripción", () -> mostrarInscripcionRegisterModule()));
        panel.add(Box.createVerticalStrut(10));
        panel.add(crearBotonMenu("Pagos", () -> mostrarPaymentRegisterModule()));
        panel.add(Box.createVerticalStrut(10));
        panel.add(crearBotonMenu("Validar Inscripción", () -> mostrarValidateRegistrationModule()));
        panel.add(Box.createVerticalStrut(10));
        panel.add(crearBotonMenu("Asistencia", () -> mostrarAttendanceRegisterModule()));
        panel.add(Box.createVerticalStrut(10));
        panel.add(crearBotonMenu("Certificados", () -> mostrarCertifiedRegisterModule()));
        panel.add(Box.createVerticalStrut(20));
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

        try (InputStream imgStream = getClass().getClassLoader().getResourceAsStream("images/fondo.png")) {
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
                crearPanelInicioSinImagen();
            }
        } catch (Exception e) {
            e.printStackTrace();
            // *Si no se puede cargar la imagen */
            crearPanelInicioSinImagen();
        }
        desktopPane.repaint();
        desktopPane.revalidate();
    }

    private void crearPanelInicioSinImagen() {
        JPanel panelInicio = new JPanel();
        panelInicio.setLayout(new BorderLayout());
        panelInicio.setBackground(new Color(240, 242, 245));
        panelInicio.setBounds(0, 0, desktopPane.getWidth(), desktopPane.getHeight());

        JPanel panelCentral = new JPanel();
        panelCentral.setLayout(new BoxLayout(panelCentral, BoxLayout.Y_AXIS));
        panelCentral.setBackground(new Color(240, 242, 245));
        panelCentral.setBorder(new EmptyBorder(100, 50, 100, 50));

        JLabel titulo = new JLabel("🎉 Bienvenido al Sistema de Eventos");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 32));
        titulo.setForeground(new Color(33, 37, 41));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitulo = new JLabel("Gestiona eventos, participantes y actividades desde un solo lugar");
        subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 18));
        subtitulo.setForeground(new Color(108, 117, 125));
        subtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel instruccion = new JLabel("Utiliza el menú lateral para navegar entre las diferentes funciones");
        instruccion.setFont(new Font("SansSerif", Font.ITALIC, 14));
        instruccion.setForeground(new Color(134, 142, 150));
        instruccion.setAlignmentX(Component.CENTER_ALIGNMENT);

        panelCentral.add(Box.createVerticalGlue());
        panelCentral.add(titulo);
        panelCentral.add(Box.createVerticalStrut(20));
        panelCentral.add(subtitulo);
        panelCentral.add(Box.createVerticalStrut(30));
        panelCentral.add(instruccion);
        panelCentral.add(Box.createVerticalGlue());

        panelInicio.add(panelCentral, BorderLayout.CENTER);
        desktopPane.add(panelInicio);
    }

    private void mostrarEventRegisterModule() {
        cerrarVentanas();
        EventRegisterForm registerModule = new EventRegisterForm(this);
        desktopPane.add(registerModule);
        centrarInternalFrame(registerModule);
        registerModule.setVisible(true);
    }

    private void mostrarUploadArchiveModule() {
        cerrarVentanas();
        UploadArchiveFrame uploadModule = new UploadArchiveFrame(this);
        desktopPane.add(uploadModule);
        centrarInternalFrame(uploadModule);
        uploadModule.setVisible(true);
    }

    private void mostrarParticipantRegisterModule() {
        cerrarVentanas();
        ParticipantRegisterForm participantModule = new ParticipantRegisterForm(this);
        desktopPane.add(participantModule);
        centrarInternalFrame(participantModule);
        participantModule.setVisible(true);
    }

    private void mostrarActivityRegisterModule() {
        cerrarVentanas();
        ActivityRegisterForm activityModule = new ActivityRegisterForm(this);
        desktopPane.add(activityModule);
        centrarInternalFrame(activityModule);
        activityModule.setVisible(true);
    }

    private void mostrarAttendanceRegisterModule() {
        cerrarVentanas();
        AttendanceRegisterForm attendanceModule = new AttendanceRegisterForm(this);
        desktopPane.add(attendanceModule);
        centrarInternalFrame(attendanceModule);
        attendanceModule.setVisible(true);
    }

    private void mostrarInscripcionRegisterModule() {
        cerrarVentanas();
        InscripcionRegisterForm inscripcionModule = new InscripcionRegisterForm(this);
        desktopPane.add(inscripcionModule);
        centrarInternalFrame(inscripcionModule);
        inscripcionModule.setVisible(true);
    }

    private void mostrarCertifiedRegisterModule() {
        cerrarVentanas();
        CertifiedRegisterForm certifiedModule = new CertifiedRegisterForm(this);
        desktopPane.add(certifiedModule);
        centrarInternalFrame(certifiedModule);
        certifiedModule.setVisible(true);
    }

    private void mostrarPaymentRegisterModule() {
        cerrarVentanas();
        PaymentRegisterForm paymentModule = new PaymentRegisterForm(this);
        desktopPane.add(paymentModule);
        centrarInternalFrame(paymentModule);
        paymentModule.setVisible(true);
    }

    private void mostrarValidateRegistrationModule() {
        cerrarVentanas();
        ValidateRegistrationForm validateModule = new ValidateRegistrationForm(this);
        desktopPane.add(validateModule);
        centrarInternalFrame(validateModule);
        validateModule.setVisible(true);
    }

    /**
     * Muestra el módulo de procesamiento de archivos con parámetros personalizables
     * 
     * @param filePath      ruta del archivo a procesar
     * @param logFolderPath ruta donde se guardarán los reportes
     * @param delay         velocidad de procesamiento en milisegundos
     */
    public void mostrarProcesamentArchiveModule(Path filePath, Path logFolderPath, int delay) {
        cerrarVentanas();
        ShowProcessArchive processModule = new ShowProcessArchive(filePath, logFolderPath, delay, this);
        desktopPane.add(processModule);
        centrarInternalFrame(processModule);
        processModule.setVisible(true);
    }

    public void cerrarVentanas() {
        for (JInternalFrame frame : desktopPane.getAllFrames()) {
            frame.dispose();
        }
        setTitle("Sistema de Administración de Eventos");
        desktopPane.repaint();
    }

    private void centrarInternalFrame(JInternalFrame frame) {
        Dimension desktopSize = desktopPane.getSize();
        Dimension jInternalFrameSize = frame.getSize();
        frame.setLocation((desktopSize.width - jInternalFrameSize.width) / 2,
                (desktopSize.height - jInternalFrameSize.height) / 2);
    }

    public Connection getConnection() {
        return connection;
    }

    public Path getDirectoryPath() {
        return directoryPath;
    }

    public void setDirectoryPath(Path directoryPath) {
        this.directoryPath = directoryPath;
    }
}
