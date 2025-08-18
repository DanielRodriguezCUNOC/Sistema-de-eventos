package com.hyrule.Frontend.loadarchive;

import java.awt.*;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicInternalFrameUI;

import com.hyrule.Backend.archiveprocessor.ProcessorArchive;
import com.hyrule.Frontend.AdminModule;

public class UploadArchiveFrame extends JInternalFrame {

    private JPanel panel;
    private AdminModule adminView;

    public UploadArchiveFrame(AdminModule adminView) {
        super("", true, true, true, true);
        this.adminView = adminView;
        adminView.setTitle("Cargar Archivo");

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
        panel.add(buttonCargarArchivo, gbc);
        panel.add(btnRegresar, gbc);

        add(panel);
    }

    // *Metodo para pedir al usuario que suba un archivo */
    public void pedirArchivo() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Seleccionar archivo");
        int userSelection = fileChooser.showOpenDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            String fileName = fileChooser.getSelectedFile().getName();
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();

            String mensaje = String.format(
                    "<html><div style='text-align: center;'>" +
                            "<b>¿Confirma que desea procesar este archivo?</b><br><br>" +
                            "<b>Archivo:</b> %s<br>" +
                            "<b>Ubicación:</b> %s" +
                            "</div></html>",
                    fileName, filePath);

            int confirmacion = JOptionPane.showConfirmDialog(
                    this,
                    mensaje,
                    "Confirmar Archivo Seleccionado",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (confirmacion == JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(this,
                        "Procesando archivo: " + fileName,
                        "Archivo Confirmado", JOptionPane.INFORMATION_MESSAGE);

                // *Llamamos al método para procesar el archivo */
                ProcessorArchive processor = new ProcessorArchive();
                processor.procesarArchivo(fileChooser.getSelectedFile().toPath());
            } else {
                JOptionPane.showMessageDialog(this,
                        "Operación cancelada. El archivo no fue procesado.",
                        "Operación Cancelada", JOptionPane.INFORMATION_MESSAGE);
            }

        } else {
            JOptionPane.showMessageDialog(this, "No se seleccionó ningún archivo.",
                    "Sin Selección", JOptionPane.WARNING_MESSAGE);
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
