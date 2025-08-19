package com.hyrule.Frontend.reports;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.basic.BasicInternalFrameUI;
import java.awt.*;
import java.awt.event.ActionListener;

import com.hyrule.Backend.handler.ParticipantReportHandler;
import com.hyrule.Frontend.AdminModule;

public class ParticipantReportForm extends JInternalFrame {

    private JTextField txtCodigoEvento;
    private JTextField txtTipoParticipante;
    private JTextField txtInstitucion;
    private JLabel lblCodigoEvento;
    private JLabel lblTipoParticipante;
    private JLabel lblInstitucion;
    private JButton btnGenerar;
    private JButton btnCancelar;
    private JButton btnRegresar;
    private AdminModule adminView;

    public ParticipantReportForm(AdminModule adminView) {
        super("", true, true, true, true);
        this.adminView = adminView;
        adminView.setTitle("Reporte de Participantes");

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

        lblCodigoEvento = new JLabel("Código del evento:");
        lblCodigoEvento.setFont(labelFont);
        txtCodigoEvento = createStyledTextField("Ingrese el código del evento");
        txtCodigoEvento.setPreferredSize(new Dimension(250, 35));

        lblTipoParticipante = new JLabel("Tipo de participante:");
        lblTipoParticipante.setFont(labelFont);
        txtTipoParticipante = createStyledTextField("Ingrese el tipo de participante (opcional)");
        txtTipoParticipante.setPreferredSize(new Dimension(250, 35));

        lblInstitucion = new JLabel("Institución:");
        lblInstitucion.setFont(labelFont);
        txtInstitucion = createStyledTextField("Ingrese la institución (opcional)");
        txtInstitucion.setPreferredSize(new Dimension(250, 35));

        btnGenerar = createModernButton("Generar Reporte", new Color(100, 149, 237));
        btnCancelar = createModernButton("Cancelar", new Color(220, 53, 69));
        btnRegresar = createModernButton("⬅️ Regresar", new Color(252, 141, 18));

        configurarAccionesBotones();

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(lblCodigoEvento, gbc);
        gbc.gridx = 1;
        panel.add(txtCodigoEvento, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(lblTipoParticipante, gbc);
        gbc.gridx = 1;
        panel.add(txtTipoParticipante, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(lblInstitucion, gbc);
        gbc.gridx = 1;
        panel.add(txtInstitucion, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(btnGenerar, gbc);
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
        btnGenerar.setActionCommand("GENERAR");
        btnCancelar.setActionCommand("CANCELAR");
        btnRegresar.setActionCommand("REGRESAR");

        ActionListener actionListener = e -> {
            String command = e.getActionCommand();
            switch (command) {
                case "GENERAR":
                    generarReporte();
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

        btnGenerar.addActionListener(actionListener);
        btnCancelar.addActionListener(actionListener);
        btnRegresar.addActionListener(actionListener);
    }

    private void limpiarCampos() {
        txtCodigoEvento.setText("");
        txtTipoParticipante.setText("");
        txtInstitucion.setText("");
        JOptionPane.showMessageDialog(this,
                "Formulario limpiado.",
                "Información",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void generarReporte() {

        String codigoEvento = txtCodigoEvento.getText().trim();
        String tipoParticipante = txtTipoParticipante.getText().trim();
        String institucion = txtInstitucion.getText().trim();

        ParticipantReportHandler reportHandler = new ParticipantReportHandler(adminView.getConnection());
        if (reportHandler.generarReporteForm(codigoEvento, tipoParticipante, institucion)) {
            JOptionPane.showMessageDialog(this,
                    "Reporte generado exitosamente.",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Error al generar el reporte.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }

    }

}
