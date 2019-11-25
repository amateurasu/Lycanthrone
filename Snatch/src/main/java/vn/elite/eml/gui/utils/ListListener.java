package vn.elite.eml.gui.utils;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

public class ListListener implements ListSelectionListener {

    private static Object[] selected;
    private final JTable table;
    private final DefaultTableModel model;

    public ListListener(JTable table, DefaultTableModel model) {
        this.table = table;
        this.model = model;
    }

    public static Object[] getSelected() {
        if (selected == null) {
            JOptionPane.showMessageDialog(null, "Please select one row!", "Error",
                    JOptionPane.WARNING_MESSAGE);
        }
        return selected;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        int selectedRow = table.getSelectedRow();
        int colNumber = table.getColumnCount();

        Object[] result = new Object[table.getColumnCount()];
        for (int i = 0; i < colNumber; i++) {
            result[i] = model.getValueAt(selectedRow, i);
        }
        selected = result;
    }
}
