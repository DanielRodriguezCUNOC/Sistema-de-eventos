package com.hyrule.Frontend.loadarchive;

import java.awt.*;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicInternalFrameUI;

import java.nio.file.Path;
import java.nio.file.Paths;

import com.hyrule.Frontend.AdminModule;

public class UploadArchiveFrame extends JInternalFrame {

    private JPanel panel;
    private AdminModule adminView;
    private JSpinner spinnerDelay;
    private JTextField textFieldReportPath;
    private Path reportPath = Paths.get(System.getProperty("user.home"), "reportes");

    public UploadArchiveFrame(AdminModule adminView) {
        super("", true, true, true, true);
        this.adminView = adminView;
        adminView.setTitle("Cargar Archivo");

        // Inicializar ruta por defecto para reportes
        this.reportPath = Paths.get(System.getProperty("user.home"), "reportes");

        setLayout(new BorderLayout());
        setSize(1000, 750);
        initComponents();
        modificarVentana();
    }

    private void initComponents() {
        panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(new Color(245, 247, 250));
        panel.setBounds(0, 0, 1000, 740);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel labelTitulo = new JLabel("Suba un Archivo para Procesar");
        labelTitulo.setFont(new Font("SansSerif", Font.BOLD, 24));
        labelTitulo.setForeground(new Color(40, 40, 40));
        labelTitulo.setHorizontalAlignment(SwingConstants.CENTER);

        // Panel para configuración de velocidad
        JPanel panelVelocidad = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelVelocidad.setBackground(new Color(245, 247, 250));

        JLabel labelVelocidad = new JLabel("Velocidad de procesamiento (ms):");
        labelVelocidad.setFont(new Font("SansSerif", Font.PLAIN, 14));

        spinnerDelay = new JSpinner(new SpinnerNumberModel(500, 500, 10000, 100));
        spinnerDelay.setFont(new Font("SansSerif", Font.PLAIN, 14));

        panelVelocidad.add(labelVelocidad);
        panelVelocidad.add(spinnerDelay);

        // Panel para ruta de reportes
        JPanel panelReportes = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelReportes.setBackground(new Color(245, 247, 250));

        JLabel labelRuta = new JLabel("Ruta de reportes:");
        labelRuta.setFont(new Font("SansSerif", Font.PLAIN, 14));

        textFieldReportPath = new JTextField(reportPath.toString(), 30);
        textFieldReportPath.setEditable(false);
        textFieldReportPath.setFont(new Font("SansSerif", Font.PLAIN, 12));

        JButton btnSeleccionarRuta = new JButton("📁 Seleccionar");
        btnSeleccionarRuta.setFont(new Font("SansSerif", Font.PLAIN, 12));
        btnSeleccionarRuta.setBackground(new Color(108, 192, 134));
        btnSeleccionarRuta.setForeground(Color.WHITE);
        btnSeleccionarRuta.setFocusPainted(false);
        btnSeleccionarRuta.setBorder(new EmptyBorder(8, 15, 8, 15));
        btnSeleccionarRuta.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSeleccionarRuta.addActionListener(e -> seleccionarRutaReportes());

        panelReportes.add(labelRuta);
        panelReportes.add(textFieldReportPath);
        panelReportes.add(btnSeleccionarRuta);

        JButton buttonCargarArchivo = new JButton("📂 Cargar Archivo");
        buttonCargarArchivo.setFont(new Font("SansSerif", Font.PLAIN, 16));
        buttonCargarArchivo.setBackground(new Color(33, 141, 230));
        buttonCargarArchivo.setForeground(Color.WHITE);
        buttonCargarArchivo.setFocusPainted(false);
        buttonCargarArchivo.setBorder(new EmptyBorder(12, 25, 12, 25));
        buttonCargarArchivo.setCursor(new Cursor(Cursor.HAND_CURSOR));

        buttonCargarArchivo.addActionListener(e -> {
            pedirArchivo();
        });

        JPanel panelTextos = new JPanel();
        panelTextos.setLayout(new BoxLayout(panelTextos, BoxLayout.Y_AXIS));
        panelTextos.setBackground(new Color(245, 247, 250));
        panelTextos.add(labelTitulo);
        panelTextos.add(Box.createVerticalStrut(10));

        JButton btnRegresar = new JButton("⬅️ Regresar");
        btnRegresar.setFont(new Font("SansSerif", Font.PLAIN, 16));
        btnRegresar.setBackground(new Color(252, 141, 18));
        btnRegresar.setForeground(Color.BLACK);
        btnRegresar.setFocusPainted(false);
        btnRegresar.setBorder(new EmptyBorder(12, 25, 12, 25));
        btnRegresar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRegresar.addActionListener(e -> {
            adminView.cerrarVentanas();
        });

        // *Añadimos elementos al panel principal */
        panel.add(panelTextos, gbc);
        panel.add(panelVelocidad, gbc);
        panel.add(panelReportes, gbc);
        panel.add(buttonCargarArchivo, gbc);
        panel.add(btnRegresar, gbc);

        add(panel);
    }

    /**
     * Método para seleccionar la ruta donde se almacenarán los reportes
     */
    private void seleccionarRutaReportes() {
        JFileChooser dirChooser = new JFileChooser();
        dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        dirChooser.setDialogTitle("Seleccionar carpeta para reportes");
        dirChooser.setCurrentDirectory(reportPath.toFile());

        int result = dirChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            reportPath = dirChooser.getSelectedFile().toPath();
            adminView.setDirectoryPath(reportPath);
            textFieldReportPath.setText(reportPath.toString());
        }
    }

    /**
     * Método para pedir al usuario que suba un archivo para procesar.
     * Utiliza un JFileChooser para seleccionar el archivo y luego inicia el
     * procesamiento.
     */

    private void pedirArchivo() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Seleccionar archivo");
        int userSelection = fileChooser.showOpenDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            // Obtener valores de la configuración
            int delay = (Integer) spinnerDelay.getValue();

            if (selectedFile.exists()) {
                adminView.mostrarProcesamentArchiveModule(selectedFile.toPath(),
                        reportPath,
                        delay);
            } else {
                JOptionPane.showMessageDialog(this, "El archivo seleccionado no existe.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // *Modificar la ventana */
    private void modificarVentana() {
        // * Quitar la barra de título*/
        BasicInternalFrameUI ui = (BasicInternalFrameUI) this.getUI();
        if (ui != null) {
            ui.setNorthPane(null);
        }

        // * Quitar el borde de la ventana */
        setBorder(BorderFactory.createEmptyBorder());
    }

}
