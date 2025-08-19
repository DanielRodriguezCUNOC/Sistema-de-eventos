package com.hyrule.Frontend.certified;

import com.hyrule.Frontend.AdminModule;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.basic.BasicInternalFrameUI;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;

import com.hyrule.Backend.model.certified.CertifiedModel;
import com.hyrule.Backend.handler.CertifiedRegisterHandler;

public class CertifiedRegisterForm extends JInternalFrame {

    private JTextField txtCorreo;
    private JTextField txtCodigoEvento;
    private JLabel lblCorreo;
    private JLabel lblCodigoEvento;
    private JButton btnRegistrar;
    private JButton btnCancelar;
    private JButton btnRegresar;
    private AdminModule adminView;

    public CertifiedRegisterForm(AdminModule adminView) {
        super("", true, true, true, true);
        this.adminView = adminView;
        adminView.setTitle("Registro de Certificados");

        setLayout(new BorderLayout());
        setSize(1000, 750);
        initComponents();
        modificarVentana();
    }

    private JTextField createStyledTextField(String placeholder) {
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 14);
        Color fieldBorderColor = new Color(200, 200, 200);
        Color fieldFocusColor = new Color(100, 149, 237);

        JTextField field = new JTextField(placeholder) {
            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (isFocusOwner()) {
                    g2.setColor(fieldFocusColor);
                    g2.setStroke(new BasicStroke(2));
                } else {
                    g2.setColor(fieldBorderColor);
                }
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
            }
        };

        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(Color.GRAY);
                }
            }
        });
        field.setFont(fieldFont);
        field.setOpaque(false);
        field.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        field.setBackground(new Color(255, 255, 255, 200));
        return field;
    }

    private JButton createModernButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void initComponents() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                setBackground(new Color(245, 245, 245));
            }
        };

        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(20, 20, 20, 20),
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(100, 149, 237), 2, true),
                        "",
                        TitledBorder.CENTER,
                        TitledBorder.TOP,
                        new Font("SansSerif", Font.BOLD, 18),
                        new Color(60, 60, 60))));

        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font labelFont = new Font("SansSerif", Font.BOLD, 16);

        lblCorreo = new JLabel("Correo del participante:");
        lblCorreo.setFont(labelFont);
        txtCorreo = createStyledTextField("Ingrese el correo del participante");
        txtCorreo.setPreferredSize(new Dimension(250, 35));

        lblCodigoEvento = new JLabel("Código de la actividad:");
        lblCodigoEvento.setFont(labelFont);
        txtCodigoEvento = createStyledTextField("Ingrese el código de la actividad");
        txtCodigoEvento.setPreferredSize(new Dimension(250, 35));

        btnRegistrar = createModernButton("Registrar", new Color(100, 149, 237));

        btnCancelar = createModernButton("Cancelar", new Color(220, 53, 69));

        btnRegresar = createModernButton("⬅️ Regresar", new Color(252, 141, 18));

        configurarAccionesBotones();

        // Ubicar componentes
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(lblCorreo, gbc);
        gbc.gridx = 1;
        panel.add(txtCorreo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(lblCodigoEvento, gbc);
        gbc.gridx = 1;
        panel.add(txtCodigoEvento, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(btnRegistrar, gbc);
        gbc.gridx = 1;
        panel.add(btnCancelar, gbc);
        gbc.gridx = 2;
        panel.add(btnRegresar, gbc);

        add(panel, BorderLayout.CENTER);
    }

    private void modificarVentana() {
        BasicInternalFrameUI ui = (BasicInternalFrameUI) this.getUI();
        if (ui != null) {
            ui.setNorthPane(null);
        }
        setBorder(BorderFactory.createEmptyBorder());
    }

    private void configurarAccionesBotones() {
        btnRegistrar.setActionCommand("REGISTRAR");
        btnCancelar.setActionCommand("CANCELAR");
        btnRegresar.setActionCommand("REGRESAR");

        ActionListener actionListener = e -> {
            String command = e.getActionCommand();
            switch (command) {
                case "REGISTRAR":
                    registrarCertificado();
                    break;
                case "CANCELAR":
                    limpiarCampos();
                    break;
                case "REGRESAR":
                    if (adminView != null)
                        adminView.cerrarVentanas();
                    else
                        dispose();
                    break;
            }
        };

        btnRegistrar.addActionListener(actionListener);
        btnCancelar.addActionListener(actionListener);
        btnRegresar.addActionListener(actionListener);
    }

    private void limpiarCampos() {
        txtCorreo.setText("");
        txtCodigoEvento.setText("");
        JOptionPane.showMessageDialog(this,
                "Formulario limpiado.",
                "Información",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void registrarCertificado() {

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Seleccionar archivo");
        File selectedFile = null;
        int userSelection = fileChooser.showOpenDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
        }

        String correo = txtCorreo.getText().trim();
        String codigoEvento = txtCodigoEvento.getText().trim();

        CertifiedModel certified = new CertifiedModel(correo, codigoEvento);

        CertifiedRegisterHandler validator = new CertifiedRegisterHandler(adminView.getConnection());

        String validationMsg = validator.validateForm(certified);
        if (!"Ok".equals(validationMsg)) {
            if (validator.insertFromForm(certified, selectedFile.toPath())) {
                JOptionPane.showMessageDialog(this,
                        "Certificado registrado correctamente.",
                        "Registro Exitoso",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Error al registrar el certificado.",
                        "Error de Registro",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    validationMsg,
                    "Error de Validación",
                    JOptionPane.WARNING_MESSAGE);
        }
    }
}
