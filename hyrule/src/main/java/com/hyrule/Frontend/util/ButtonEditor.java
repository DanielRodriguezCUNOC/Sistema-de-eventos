package com.hyrule.Frontend.util;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Editor de botones para celdas de tabla con funcionalidad personalizada
 */
public class ButtonEditor extends DefaultCellEditor {

    private JButton button;
    private String label;
    private boolean clicked;
    private int row;
    private ActionListener actionListener;

    public ButtonEditor(JCheckBox checkBox, ActionListener actionListener) {
        super(checkBox);
        this.actionListener = actionListener;

        button = new JButton();
        button.setOpaque(true);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        button.addActionListener(e -> fireEditingStopped());
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column) {
        this.label = value != null ? value.toString() : "";
        button.setText(label);

        if ("Validar".equalsIgnoreCase(label)) {
            button.setBackground(new Color(40, 167, 69)); // Verde
        } else if ("Invalidar".equalsIgnoreCase(label)) {
            button.setBackground(new Color(220, 53, 69)); // Rojo
        }

        this.row = row;
        clicked = true;
        return button;
    }

    @Override
    public Object getCellEditorValue() {
        if (clicked) {
            actionListener
                    .actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "" + row));
        }
        clicked = false;
        return label;
    }

    @Override
    public boolean stopCellEditing() {
        clicked = false;
        return super.stopCellEditing();
    }
}
