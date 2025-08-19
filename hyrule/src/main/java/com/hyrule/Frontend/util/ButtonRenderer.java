package com.hyrule.Frontend.util;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * Renderizador de botones para celdas de tabla
 */
public class ButtonRenderer extends JButton implements TableCellRenderer {

    public ButtonRenderer(String text, Color background) {
        setText(text);
        setOpaque(true);
        setBackground(background);
        setForeground(Color.WHITE);
        setFocusPainted(false);
        setBorderPainted(false);
        setFont(new Font("SansSerif", Font.BOLD, 12));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus,
            int row, int column) {
        setText(value != null ? value.toString() : "");
        return this;
    }
}
