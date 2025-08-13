package com.hyrule.Frontend.participant;

import com.hyrule.Backend.Validations.ValidateParticipantRegister;
import com.hyrule.Backend.handler.ParticipantRegisterHandler;
import com.hyrule.Frontend.AdminModule;

import java.awt.*;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.TitledBorder;
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

        setLayout(new BorderLayout());
        setSize(1000, 740);
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

    private void initComponents() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                setBackground(new Color(245, 245, 245));
            }
        };
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 20, 20, 20), // Reducir padding superior
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(100, 149, 237), 2, true),
                        "Registro de Participante",
                        TitledBorder.CENTER,
                        TitledBorder.TOP,
                        new Font("SansSerif", Font.BOLD, 18), // Aumentar tamaño del título
                        new Color(60, 60, 60))));

        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 15, 5, 15); // Reducir espaciado vertical, aumentar horizontal
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTH; // Anclar hacia arriba

        Font labelFont = new Font("SansSerif", Font.BOLD, 16); // Hacer labels más grandes y negritas
        Font fieldFont = new Font("SansSerif", Font.PLAIN, 16); // Hacer campos más grandes

        lblCorreo = new JLabel("Correo Electrónico:");
        lblCorreo.setFont(labelFont);
        txtCorreo = new JTextField();
        txtCorreo.setFont(fieldFont);
        txtCorreo.setPreferredSize(new Dimension(250, 35)); // Hacer campo más grande

        lblNombre = new JLabel("Nombre Completo:");
        lblNombre.setFont(labelFont);
        txtNombre = new JTextField();
        txtNombre.setFont(fieldFont);
        txtNombre.setPreferredSize(new Dimension(250, 35)); // Hacer campo más grande

        lblInstitucion = new JLabel("Institución:");
        lblInstitucion.setFont(labelFont);
        txtInstitucion = new JTextField();
        txtInstitucion.setFont(fieldFont);
        txtInstitucion.setPreferredSize(new Dimension(250, 35)); // Hacer campo más grande

        lblTipoParticipante = new JLabel("Tipo de Participante:");
        lblTipoParticipante.setFont(labelFont);
        comboTipoParticipante = new JComboBox<>(new String[] { "ESTUDIANTE", "PROFESIONAL", "INVITADO" });
        comboTipoParticipante.setFont(fieldFont);
        comboTipoParticipante.setPreferredSize(new Dimension(250, 35)); // Hacer combo más grande

        btnRegistrar = new JButton("Registrar");
        btnRegistrar.setFont(new Font("SansSerif", Font.BOLD, 16));
        btnRegistrar.setForeground(new Color(255, 255, 255));
        btnRegistrar.setBackground(new Color(0, 123, 255));

        btnCancelar = new JButton("Cancelar");
        btnCancelar.setFont(new Font("SansSerif", Font.BOLD, 16));
        btnCancelar.setForeground(new Color(255, 255, 255));
        btnCancelar.setBackground(new Color(220, 53, 69));

        btnRegresar = new JButton("Regresar");
        btnRegresar.setFont(new Font("SansSerif", Font.BOLD, 16));
        btnRegresar.setForeground(new Color(255, 255, 255));
        btnRegresar.setBackground(new Color(108, 117, 125));

        // *Configurar acciones de botones */
        configurarAccionesBotones();

        // *Organizamos los componentes en el panel */
        // Empezar desde la fila 0 para estar más cerca del título
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        panel.add(lblCorreo, gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(txtCorreo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(lblNombre, gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(txtNombre, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(lblInstitucion, gbc);
        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(txtInstitucion, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(lblTipoParticipante, gbc);
        gbc.gridx = 1;
        gbc.gridy = 3;
        panel.add(comboTipoParticipante, gbc);

        // Agregar espacio adicional antes de los botones
        gbc.insets = new Insets(20, 15, 5, 15);
        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(btnRegistrar, gbc);
        gbc.gridx = 1;
        gbc.gridy = 5;
        panel.add(btnCancelar, gbc);
        gbc.gridx = 2;
        gbc.gridy = 5;
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

        // *Validar los campos */
        ValidateParticipantRegister validator = new ValidateParticipantRegister(correo, nombre, institucion,
                tipoParticipante);

        if (validator.isValid()) {
            // * Si los campos son válidos, procesar el registro del participante */
            ParticipantRegisterHandler participantHandler = new ParticipantRegisterHandler();
            boolean exito = participantHandler.registerParticipantFromForm(correo, nombre, tipoParticipante,
                    institucion);
            if (exito) {
                JOptionPane.showMessageDialog(this, "Participante registrado exitosamente.", "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Error al registrar el participante.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Error de validación", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

}
