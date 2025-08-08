package com.hyrule.Frontend.event;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.basic.BasicInternalFrameUI;

import com.hyrule.Backend.handler.EventRegisterHandler;
import com.hyrule.Frontend.AdminModule;
import com.hyrule.Validations.ValidateEventRegister;

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
        setSize(600, 450);
        initComponents();

        modificarVentana();

    }

    // *Creamos los componentes */

    private void initComponents() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                setBackground(new Color(245, 245, 245)); // fondo gris claro
            }
        };
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(20, 10, 10, 10),
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(100, 149, 237), 2, true),
                        "Registro de Evento",
                        TitledBorder.CENTER,
                        TitledBorder.TOP,
                        new Font("SansSerif", Font.BOLD, 16),
                        new Color(60, 60, 60))));

        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font labelFont = new Font("SansSerif", Font.PLAIN, 14);
        Font fieldFont = new Font("SansSerif", Font.PLAIN, 14);

        JLabel lblCodigo = new JLabel("Código Evento:");
        lblCodigo.setFont(labelFont);
        txtCodigoEvento = new JTextField();
        txtCodigoEvento.setFont(fieldFont);

        JLabel lblFecha = new JLabel("Fecha Evento:");
        lblFecha.setFont(labelFont);
        txtFechaEvento = new JTextField("YYYY-MM-DD");
        txtFechaEvento.setFont(fieldFont);
        txtFechaEvento.setForeground(Color.GRAY);

        JLabel lblTipo = new JLabel("Tipo Evento:");
        lblTipo.setFont(labelFont);
        comboTipoEvento = new JComboBox<>(new String[] { "CHARLA", "CONGRESO", "TALLER", "DEBATE" });
        comboTipoEvento.setFont(fieldFont);

        JLabel lblTitulo = new JLabel("Título:");
        lblTitulo.setFont(labelFont);
        txtTitulo = new JTextField();
        txtTitulo.setFont(fieldFont);

        JLabel lblUbicacion = new JLabel("Ubicación:");
        lblUbicacion.setFont(labelFont);
        txtUbicacion = new JTextField();
        txtUbicacion.setFont(fieldFont);

        JLabel lblCupoMax = new JLabel("Cupo Máximo:");
        lblCupoMax.setFont(labelFont);
        spinnerCupoMax = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
        spinnerCupoMax.setFont(fieldFont);

        btnRegistrar = new JButton("Registrar");
        btnRegistrar.setBackground(new Color(100, 149, 237));
        btnRegistrar.setForeground(Color.WHITE);
        btnRegistrar.setFont(new Font("SansSerif", Font.BOLD, 14));

        btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(new Color(220, 53, 69));
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setFont(new Font("SansSerif", Font.BOLD, 14));

        btnRegresar = new JButton("Inicio");
        btnRegresar.setBackground(new Color(108, 117, 125));
        btnRegresar.setFocusPainted(false);
        btnRegresar.setForeground(Color.WHITE);
        btnRegresar.setFont(new Font("SansSerif", Font.BOLD, 14));

        // *Agregamos acciones a los botones */
        configurarAccionesBotones();

        // *Organizamos los componentes en el panel*/

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(lblCodigo, gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(txtCodigoEvento, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(lblFecha, gbc);
        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(txtFechaEvento, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(lblTipo, gbc);
        gbc.gridx = 1;
        gbc.gridy = 3;
        panel.add(comboTipoEvento, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(lblTitulo, gbc);
        gbc.gridx = 1;
        gbc.gridy = 4;
        panel.add(txtTitulo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(lblUbicacion, gbc);
        gbc.gridx = 1;
        gbc.gridy = 5;
        panel.add(txtUbicacion, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        panel.add(lblCupoMax, gbc);
        gbc.gridx = 1;
        gbc.gridy = 6;
        panel.add(spinnerCupoMax, gbc);

        gbc.gridx = 0;
        gbc.gridy = 7;
        panel.add(btnRegistrar, gbc);
        gbc.gridx = 1;
        gbc.gridy = 7;
        panel.add(btnCancelar, gbc);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
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
            JOptionPane.showMessageDialog(this, "Por favor, complete todos los campos correctamente.", "Error",
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
