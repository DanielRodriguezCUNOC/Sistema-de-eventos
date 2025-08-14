package com.hyrule.Frontend.activity;

import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.plaf.basic.BasicInternalFrameUI;

import com.hyrule.Backend.handler.ActivityRegisterHandler;
import com.hyrule.Backend.model.activity.ActivityModel;
import com.hyrule.Frontend.AdminModule;

import javax.swing.border.TitledBorder;

public class ActivityRegisterForm extends JInternalFrame {

    private JTextField txtCodigoActividad;
    private JTextField txtCodigoEvento;
    private JComboBox<String> comboTipoActividad;
    private JTextField txtTitulo;
    private JTextField txtCorreoPonente;
    private JTextField txtHoraInicio;
    private JTextField txtHoraFin;
    private JSpinner spinnerCupoMaximo;
    private JButton btnRegistrar;
    private JButton btnCancelar;
    private JButton btnRegresar;
    AdminModule adminView;

    public ActivityRegisterForm(AdminModule adminView) {
        super("", true, true, true, true);
        this.adminView = adminView;

        setLayout(new BorderLayout());
        setSize(1000, 740);
        initComponents();
        modificarVentana();
    }

    public ActivityRegisterForm() {
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
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(100, 149, 237), 2, true),
                "Registro de Actividad",
                TitledBorder.CENTER,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 18),
                new Color(60, 60, 60)));
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font labelFont = new Font("Segoe UI", Font.PLAIN, 14);

        txtCodigoActividad = createStyledTextField("ACT-00000001");
        txtCodigoEvento = createStyledTextField("EVT-00000001");
        comboTipoActividad = new JComboBox<>(new String[] { "CHARLA", "TALLER", "DEBATE", "OTRA" });
        comboTipoActividad.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtTitulo = createStyledTextField("Taller de pociones");
        txtCorreoPonente = createStyledTextField("Correo del Ponente");
        txtHoraInicio = createStyledTextField("10:00)");
        txtHoraFin = createStyledTextField("12:00");

        spinnerCupoMaximo = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
        spinnerCupoMaximo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        spinnerCupoMaximo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));

        btnRegistrar = createModernButton("Registrar", new Color(100, 149, 237));
        btnCancelar = createModernButton("Cancelar", new Color(220, 53, 69));
        btnRegresar = createModernButton("Regresar", new Color(108, 117, 125));

        configurarAccionesBotones();

        // Labels y disposición
        String[] labels = {
                "Código de la Actividad:", "Código del Evento:",
                "Tipo de Actividad:", "Título de la Actividad:",
                "Correo del Ponente:", "Hora de Inicio (hh:mm):",
                "Hora de Fin (hh:mm):", "Cupo Máximo:"
        };
        Component[] fields = {
                txtCodigoActividad, txtCodigoEvento, comboTipoActividad, txtTitulo,
                txtCorreoPonente, txtHoraInicio, txtHoraFin, spinnerCupoMaximo
        };

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            panel.add(new JLabel(labels[i]) {
                {
                    setFont(labelFont);
                }
            }, gbc);
            gbc.gridx = 1;
            panel.add(fields[i], gbc);
        }

        // Botones
        gbc.insets = new Insets(25, 15, 5, 15);
        gbc.gridx = 0;
        gbc.gridy = labels.length;
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
            String cmd = e.getActionCommand();
            switch (cmd) {
                case "REGISTRAR":
                    registrarActividad();
                    break;
                case "CANCELAR":
                    limpiarCampos();
                    break;
                case "REGRESAR":
                    adminView.cerrarVentanas();
                    break;
            }
        };

        btnRegistrar.addActionListener(actionListener);
        btnCancelar.addActionListener(actionListener);
        btnRegresar.addActionListener(actionListener);
    }

    private void limpiarCampos() {
        txtCodigoActividad.setText("");
        txtCodigoEvento.setText("");
        comboTipoActividad.setSelectedIndex(0);
        txtTitulo.setText("");
        txtCorreoPonente.setText("");
        txtHoraInicio.setText("");
        txtHoraFin.setText("");
        spinnerCupoMaximo.setValue(1);
        JOptionPane.showMessageDialog(this, "Registro cancelado.", "Información", JOptionPane.INFORMATION_MESSAGE);
    }

    private void registrarActividad() {

        String codigoActividad = txtCodigoActividad.getText().trim();
        String codigoEvento = txtCodigoEvento.getText().trim();
        String tipoActividad = (String) comboTipoActividad.getSelectedItem();
        String titulo = txtTitulo.getText().trim();
        String correoPonente = txtCorreoPonente.getText().trim();
        String horaInicio = txtHoraInicio.getText().trim();
        String horaFin = txtHoraFin.getText().trim();
        int cupoMaximo = (Integer) spinnerCupoMaximo.getValue();

        ActivityModel actividad = new ActivityModel(codigoActividad, codigoEvento, tipoActividad, titulo, correoPonente,
                horaInicio, horaFin, cupoMaximo);

        ActivityRegisterHandler handler = new ActivityRegisterHandler();
        if (!handler.isValid(actividad)) {
            JOptionPane.showMessageDialog(this, "Error al registrar la actividad. Verifique los datos ingresados.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;

        } else {
            JOptionPane.showMessageDialog(this, "Actividad registrada exitosamente.", "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
        }

    }
}
