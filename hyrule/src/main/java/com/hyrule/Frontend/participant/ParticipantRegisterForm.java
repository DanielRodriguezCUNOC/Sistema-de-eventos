package com.hyrule.Frontend.participant;

import com.hyrule.Backend.handler.ParticipantRegisterHandler;
import com.hyrule.Backend.model.participant.ParticipantModel;
import com.hyrule.Frontend.AdminModule;

import java.awt.*;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.plaf.basic.BasicInternalFrameUI;

public class ParticipantRegisterForm extends JInternalFrame {

    private JTextField txtCorreo;
    private JTextField txtNombre;
    private JComboBox<String> comboTipoParticipante;
    private JTextField txtInstitucion;
    private JLabel lblCorreo;
    private JLabel lblNombre;
    private JLabel lblTipoParticipante;
    private JLabel lblInstitucion;
    private JButton btnRegistrar;
    private JButton btnCancelar;
    private JButton btnRegresar;
    private AdminModule adminView;

    public ParticipantRegisterForm(AdminModule adminView) {
        super("", true, true, true, true);
        this.adminView = adminView;
        adminView.setTitle("Registro de Participantes");

        setLayout(new BorderLayout());
        setSize(1000, 750);
        initComponents();

        modificarVentana();
    }

    public ParticipantRegisterForm() {
        super("", true, true, true, true);
        setLayout(new BorderLayout());
        setSize(1000, 740);
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
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(245, 245, 245),
                        0, getHeight(), new Color(230, 230, 230));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(new Color(0, 0, 0, 30));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                g2.dispose();
            }
        };
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font labelFont = new Font("Segoe UI", Font.PLAIN, 14);

        // Campos de texto
        txtCorreo = createStyledTextField("zelda@hyrule.edu");
        txtNombre = createStyledTextField(" Zelda Hyrule");
        txtInstitucion = createStyledTextField("Universidad de Hyrule");

        // ComboBox moderno
        comboTipoParticipante = new JComboBox<>(new String[] { "ESTUDIANTE", "PROFESIONAL", "INVITADO" });
        comboTipoParticipante.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboTipoParticipante.setBackground(Color.WHITE);
        comboTipoParticipante.setFocusable(false);
        comboTipoParticipante.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));

        // Botones modernos
        btnRegistrar = createModernButton("Registrar", new Color(100, 149, 237));
        btnCancelar = createModernButton("Cancelar", new Color(220, 53, 69));
        btnRegresar = createModernButton("⬅️ Regresar", new Color(252, 141, 18));

        configurarAccionesBotones();

        // Labels
        lblCorreo = new JLabel("Correo Electrónico:");
        lblCorreo.setFont(labelFont);
        lblNombre = new JLabel("Nombre Completo:");
        lblNombre.setFont(labelFont);
        lblInstitucion = new JLabel("Institución:");
        lblInstitucion.setFont(labelFont);
        lblTipoParticipante = new JLabel("Tipo de Participante:");
        lblTipoParticipante.setFont(labelFont);

        // Posicionamiento con GridBag
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(lblCorreo, gbc);
        gbc.gridx = 1;
        panel.add(txtCorreo, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(lblNombre, gbc);
        gbc.gridx = 1;
        panel.add(txtNombre, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(lblInstitucion, gbc);
        gbc.gridx = 1;
        panel.add(txtInstitucion, gbc);
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(lblTipoParticipante, gbc);
        gbc.gridx = 1;
        panel.add(comboTipoParticipante, gbc);

        gbc.insets = new Insets(20, 15, 5, 15);
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(btnRegistrar, gbc);
        gbc.gridx = 1;
        panel.add(btnCancelar, gbc);
        gbc.gridx = 2;
        panel.add(btnRegresar, gbc);

        add(panel, BorderLayout.CENTER);
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

    // *Configurar acciones de botones */
    private void configurarAccionesBotones() {
        btnRegistrar.setActionCommand("REGISTRAR");
        btnCancelar.setActionCommand("CANCELAR");
        btnRegresar.setActionCommand("REGRESAR");

        ActionListener actionListener = e -> {
            String command = e.getActionCommand();
            switch (command) {
                case "REGISTRAR":
                    registrarParticipante();
                    break;
                case "CANCELAR":
                    cancelEventAction();
                    break;
                case "REGRESAR":
                    if (adminView != null) {
                        adminView.cerrarVentanas();
                    } else {
                        dispose();
                    }
                    break;
            }
        };

        btnRegistrar.addActionListener(actionListener);
        btnCancelar.addActionListener(actionListener);
        btnRegresar.addActionListener(actionListener);
    }

    private void cancelEventAction() {
        // *Limpiar los campos */
        txtCorreo.setText("");
        txtNombre.setText("");
        txtInstitucion.setText("");
        comboTipoParticipante.setSelectedIndex(0);
        JOptionPane.showMessageDialog(this, "Registro cancelado.", "Información", JOptionPane.INFORMATION_MESSAGE);
    }

    private void registrarParticipante() {
        String correo = txtCorreo.getText().trim();
        String nombre = txtNombre.getText().trim();
        String institucion = txtInstitucion.getText().trim();
        String tipoParticipante = (String) comboTipoParticipante.getSelectedItem();

        ParticipantModel participante = new ParticipantModel(correo, nombre, tipoParticipante, institucion);

        ParticipantRegisterHandler validator = new ParticipantRegisterHandler(adminView.getConnection());

        String validationMsg = validator.validateForm(participante);

        if ("Ok".equals(validationMsg)) {
            if (validator.insertFromForm(participante)) {
                JOptionPane.showMessageDialog(this, "Participante registrado exitosamente.", "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Error al registrar el participante.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, validationMsg, "Error de Validación", JOptionPane.WARNING_MESSAGE);

        }
    }

}
