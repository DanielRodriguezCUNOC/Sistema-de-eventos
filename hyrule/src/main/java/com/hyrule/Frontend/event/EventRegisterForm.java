package com.hyrule.Frontend.event;

import javax.swing.*;

import com.hyrule.Backend.persistence.event.EventRegisterData;
import com.hyrule.Validations.ValidateEventRegister;

import java.awt.*;

public class EventRegisterForm extends JFrame {
     private JTextField txtCodigoEvento;
    private JTextField txtFechaEvento;
    private JComboBox<String> comboTipoEvento;
    private JTextField txtTitulo;
    private JTextField txtUbicacion;
    private JSpinner spinnerCupoMax;
    private JButton btnRegistrar;
    private JButton btnCancelar;
    
    
    public EventRegisterForm() {
        setTitle("Event Registration Form");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
        
        
    }

    //*Creamos los componentes */

    private void initComponents(){
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblCodigo = new JLabel("Código Evento:");
        txtCodigoEvento = new JTextField();

        JLabel lblFecha = new JLabel("Fecha Evento:");
        txtFechaEvento = new JTextField("YYYY-MM-DD"); //*Mostramos el formato a ingresar */

        JLabel lblTipo = new JLabel("Tipo Evento:");
        comboTipoEvento = new JComboBox<>(new String[]{"CHARLA", "CONGRESO", "TALLER", "DEBATE"});

        JLabel lblTitulo = new JLabel("Título:");
        txtTitulo = new JTextField();

        JLabel lblUbicacion = new JLabel("Ubicación:");
        txtUbicacion = new JTextField();

        JLabel lblCupoMax = new JLabel("Cupo Máximo:");
        spinnerCupoMax = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));

        btnRegistrar = new JButton("Registrar");
        btnCancelar = new JButton("Cancelar");

        btnRegistrar.addActionListener(e -> registrarEvento());

        gbc.gridx = 0; gbc.gridy = 0; panel.add(lblCodigo, gbc);
        gbc.gridx = 1; gbc.gridy = 0; panel.add(txtCodigoEvento, gbc);

        gbc.gridx = 0; gbc.gridy = 1; panel.add(lblFecha, gbc);
        gbc.gridx = 1; gbc.gridy = 1; panel.add(txtFechaEvento, gbc);

        gbc.gridx = 0; gbc.gridy = 2; panel.add(lblTipo, gbc);
        gbc.gridx = 1; gbc.gridy = 2; panel.add(comboTipoEvento, gbc);

        gbc.gridx = 0; gbc.gridy = 3; panel.add(lblTitulo, gbc);
        gbc.gridx = 1; gbc.gridy = 3; panel.add(txtTitulo, gbc);

        gbc.gridx = 0; gbc.gridy = 4; panel.add(lblUbicacion, gbc);
        gbc.gridx = 1; gbc.gridy = 4; panel.add(txtUbicacion, gbc);

        gbc.gridx = 0; gbc.gridy = 5; panel.add(lblCupoMax, gbc);
        gbc.gridx = 1; gbc.gridy = 5; panel.add(spinnerCupoMax, gbc);

        gbc.gridx = 0; gbc.gridy = 6; panel.add(btnRegistrar, gbc);
        gbc.gridx = 1; gbc.gridy = 6; panel.add(btnCancelar, gbc);

        add(panel);
    }

    private void registrarEvento(){

         String codigo = txtCodigoEvento.getText().trim();
        String fechaStr = txtFechaEvento.getText().trim();
        String tipoStr = (String) comboTipoEvento.getSelectedItem();
        String titulo = txtTitulo.getText().trim();
        String ubicacion = txtUbicacion.getText().trim();
        int cupoMax = (Integer) spinnerCupoMax.getValue();

        ValidateEventRegister validator = new ValidateEventRegister(codigo, fechaStr, tipoStr, titulo, ubicacion, cupoMax);

        if (validator.isValid()) {
            EventRegisterData eventData = new EventRegisterData(codigo, fechaStr, tipoStr, titulo, ubicacion, cupoMax);
            boolean success = eventData.registrarEvento();
            if (success) {
                JOptionPane.showMessageDialog(this, "Evento registrado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                dispose(); // Cierra el formulario
            } else {
                JOptionPane.showMessageDialog(this, "Error al registrar el evento.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Por favor, complete todos los campos correctamente.", "Error", JOptionPane.ERROR_MESSAGE);
            
        }


    }

    
    public static void main(String[] args) {
        EventRegisterForm registerForm = new EventRegisterForm();
        registerForm.setVisible(true);
    }
    

}
