package com.hyrule.Frontend.event;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.basic.BasicInternalFrameUI;

import com.hyrule.Backend.Validations.ValidateEventRegister;
import com.hyrule.Backend.handler.EventRegisterHandler;
import com.hyrule.Frontend.AdminModule;

import java.awt.*;
import java.awt.event.ActionListener;

public class EventRegisterForm extends JInternalFrame {
    private JTextField txtCodigoEvento;
    private JTextField txtFechaEvento;
    private JComboBox<String> comboTipoEvento;
    private JTextField txtTitulo;
    private JTextField txtUbicacion;
    private JSpinner spinnerCupoMax;
    private JButton btnRegistrar;
    private JButton btnCancelar;
    private JButton btnRegresar;
    private AdminModule adminView;

    public EventRegisterForm(AdminModule adminView) {

        super("", true, true, true, true);
        this.adminView = adminView;

        setLayout(new BorderLayout());
        setSize(1000, 740);
        initComponents();

        modificarVentana();

    }

    // *Creamos los componentes */

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

        txtCodigoEvento = createStyledTextField("EVT-XXX");
        txtFechaEvento = createStyledTextField("DD/MM/YYYY");
        txtTitulo = createStyledTextField("Título del Evento");
        txtUbicacion = createStyledTextField("Ubicación del Evento");

        comboTipoEvento = new JComboBox<>(new String[] { "CHARLA", "CONGRESO", "TALLER", "DEBATE" });
        comboTipoEvento.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboTipoEvento.setBackground(Color.WHITE);
        comboTipoEvento.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));

        spinnerCupoMax = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
        spinnerCupoMax.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JSpinner.NumberEditor editor = new JSpinner.NumberEditor(spinnerCupoMax, "#");
        spinnerCupoMax.setEditor(editor);
        editor.getTextField().setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        editor.getTextField().setBackground(Color.WHITE);

        btnRegistrar = createModernButton("Registrar", new Color(100, 149, 237));
        btnCancelar = createModernButton("Cancelar", new Color(220, 53, 69));
        btnRegresar = createModernButton("⬅️ Regresar", new Color(252, 141, 18));

        configurarAccionesBotones();

        Font labelFont = new Font("Segoe UI", Font.PLAIN, 14);
        JLabel lblCodigo = new JLabel("Código Evento:");
        lblCodigo.setFont(labelFont);
        JLabel lblFecha = new JLabel("Fecha Evento:");
        lblFecha.setFont(labelFont);
        JLabel lblTipo = new JLabel("Tipo Evento:");
        lblTipo.setFont(labelFont);
        JLabel lblTitulo = new JLabel("Título:");
        lblTitulo.setFont(labelFont);
        JLabel lblUbicacion = new JLabel("Ubicación:");
        lblUbicacion.setFont(labelFont);
        JLabel lblCupoMax = new JLabel("Cupo Máximo:");
        lblCupoMax.setFont(labelFont);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(lblCodigo, gbc);
        gbc.gridx = 1;
        panel.add(txtCodigoEvento, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(lblFecha, gbc);
        gbc.gridx = 1;
        panel.add(txtFechaEvento, gbc);
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(lblTipo, gbc);
        gbc.gridx = 1;
        panel.add(comboTipoEvento, gbc);
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(lblTitulo, gbc);
        gbc.gridx = 1;
        panel.add(txtTitulo, gbc);
        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(lblUbicacion, gbc);
        gbc.gridx = 1;
        panel.add(txtUbicacion, gbc);
        gbc.gridx = 0;
        gbc.gridy = 6;
        panel.add(lblCupoMax, gbc);
        gbc.gridx = 1;
        panel.add(spinnerCupoMax, gbc);
        gbc.gridx = 0;
        gbc.gridy = 7;
        panel.add(btnRegistrar, gbc);
        gbc.gridx = 1;
        panel.add(btnCancelar, gbc);
        gbc.gridx = 2;
        panel.add(btnRegresar, gbc);

        add(panel);
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

    private void registrarEvento() {

        String codigo = txtCodigoEvento.getText().trim();
        String fechaStr = txtFechaEvento.getText().trim();
        String tipoStr = (String) comboTipoEvento.getSelectedItem();
        String titulo = txtTitulo.getText().trim();
        String ubicacion = txtUbicacion.getText().trim();
        int cupoMax = (Integer) spinnerCupoMax.getValue();

        // * Validar los campos del formulario */
        ValidateEventRegister validator = new ValidateEventRegister(codigo, fechaStr, tipoStr, titulo, ubicacion,
                cupoMax);

        if (validator.isValid()) {

            // * Si los campos son válidos, procesar el registro del evento */
            EventRegisterHandler eventHandler = new EventRegisterHandler();
            boolean exito = eventHandler.registerEventFromForm(codigo, fechaStr, tipoStr, titulo, ubicacion, cupoMax);
            if (exito) {
                JOptionPane.showMessageDialog(this, "Evento registrado exitosamente.", "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Error al registrar el evento.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Error de validación", "Error",
                    JOptionPane.ERROR_MESSAGE);

        }

    }

    // * Metodo para las acciones de los botones */

    private void configurarAccionesBotones() {

        // *Asignamos un comando para identificar cada boton */
        btnRegistrar.setActionCommand("REGISTRAR");
        btnCancelar.setActionCommand("CANCELAR");
        btnRegresar.setActionCommand("REGRESAR");

        // * Creamos un listener para todos los botones */

        ActionListener actionListener = e -> {
            String command = e.getActionCommand();
            switch (command) {
                case "REGISTRAR":
                    registerEventAction();
                    break;
                case "CANCELAR":
                    cancelEventAction();
                    break;
                case "REGRESAR":
                    backModuleAction();
                    break;
                default:
                    break;
            }
        };

        // *Asignamos el listener a los botones */
        btnRegistrar.addActionListener(actionListener);
        btnCancelar.addActionListener(actionListener);
        btnRegresar.addActionListener(actionListener);

    }

    private void registerEventAction() {

        registrarEvento();
    }

    private void cancelEventAction() {
        // * Limpiar los campos del formulario */
        txtCodigoEvento.setText("");
        txtFechaEvento.setText("YYYY-MM-DD");
        comboTipoEvento.setSelectedIndex(0);
        txtTitulo.setText("");
        txtUbicacion.setText("");
        spinnerCupoMax.setValue(1);
        txtFechaEvento.setForeground(Color.GRAY);
        txtFechaEvento.setCaretPosition(0);
        JOptionPane.showMessageDialog(this,
                "Formulario cancelado y campos limpiados.",
                "Cancelado",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void backModuleAction() {
        adminView.cerrarVentanas();
    }

}
