
package com.hyrule.Frontend.validate_registration;

import com.hyrule.Backend.handler.ValidateInscripcionRegisterHandler;
import com.hyrule.Backend.model.payment.PaymentModel;
import com.hyrule.Backend.model.validate_registration.ValidateRegistrationModel;
import com.hyrule.Backend.persistence.payment.ControlPayment;
import com.hyrule.Frontend.AdminModule;
import com.hyrule.Frontend.util.ButtonEditor;
import com.hyrule.Frontend.util.ButtonRenderer;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.basic.BasicInternalFrameUI;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class ValidateRegistrationForm extends JInternalFrame {

    private JTextField txtBuscar;
    private JTable table;
    private DefaultTableModel tableModel;
    private ControlPayment controlPayment;
    AdminModule adminView;

    public ValidateRegistrationForm(AdminModule adminView) {
        super("", true, true, true, true);
        this.adminView = adminView;
        adminView.setTitle("Validar Inscripción");
        setLayout(new BorderLayout());
        setSize(1000, 740);

        controlPayment = new ControlPayment();

        initComponents();
        modificarVentana();
        cargarDatos();
    }

    private void initComponents() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(100, 149, 237), 2, true),
                "Validar Inscripción",
                TitledBorder.CENTER,
                TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 18),
                new Color(60, 60, 60)));

        // Panel de búsqueda
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblBuscar = new JLabel("Buscar:");
        lblBuscar.setFont(new Font("SansSerif", Font.BOLD, 14));
        txtBuscar = new JTextField(30);
        JButton btnBuscar = new JButton("Filtrar");
        btnBuscar.addActionListener(this::filtrarTabla);

        searchPanel.add(lblBuscar);
        searchPanel.add(txtBuscar);
        searchPanel.add(btnBuscar);

        // Tabla
        String[] columnNames = { "Correo", "Código Evento", "Validar", "Invalidar" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column >= 2; // Solo botones
            }
        };
        table = new JTable(tableModel);
        table.setRowHeight(35);
        JScrollPane scrollPane = new JScrollPane(table);

        table.getColumn("Validar").setCellRenderer(new ButtonRenderer("Validar", new Color(40, 167, 69)));
        table.getColumn("Invalidar").setCellRenderer(new ButtonRenderer("Invalidar", new Color(220, 53, 69)));

        table.getColumn("Validar").setCellEditor(
                new ButtonEditor(new JCheckBox(), e -> validarRegistro(Integer.parseInt(e.getActionCommand()))));
        table.getColumn("Invalidar").setCellEditor(
                new ButtonEditor(new JCheckBox(), e -> invalidarRegistro(Integer.parseInt(e.getActionCommand()))));

        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        add(panel, BorderLayout.CENTER);
    }

    private void modificarVentana() {
        BasicInternalFrameUI ui = (BasicInternalFrameUI) this.getUI();
        if (ui != null) {
            ui.setNorthPane(null);
        }
        setBorder(BorderFactory.createEmptyBorder());
    }

    private void cargarDatos() {
        tableModel.setRowCount(0);
        try {
            List<PaymentModel> pagos = controlPayment.findAll();
            for (PaymentModel p : pagos) {
                tableModel.addRow(new Object[] { p.getCorreo(), p.getCodigoEvento(), "Validar", "Invalidar" });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar pagos: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filtrarTabla(ActionEvent e) {
        String filtro = txtBuscar.getText().trim().toLowerCase();
        if (filtro.isEmpty()) {
            cargarDatos();
            return;
        }

        try {
            List<PaymentModel> pagos = controlPayment.findPaymentsByCorreoOrEvento(filtro);
            for (PaymentModel p : pagos) {
                tableModel.addRow(new Object[] { p.getCorreo(), p.getCodigoEvento(), "Validar", "Invalidar" });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al filtrar pagos: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void validarRegistro(int row) {
        String correo = table.getValueAt(row, 0).toString();
        String codigoEvento = table.getValueAt(row, 1).toString();
        try {
            ValidateRegistrationModel registro = new ValidateRegistrationModel(correo, codigoEvento);
            ValidateInscripcionRegisterHandler handler = new ValidateInscripcionRegisterHandler();
            if (handler.isValid(registro)) {

                JOptionPane.showMessageDialog(this, "Inscripción validada correctamente.",
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarDatos(); // Recargar datos después de validar
            } else {
                JOptionPane.showMessageDialog(this, "Error al validar inscripción.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al validar inscripción: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void invalidarRegistro(int row) {
        JOptionPane.showMessageDialog(this, "Inscripción invalidada.",
                "Información", JOptionPane.INFORMATION_MESSAGE);
    }
}
