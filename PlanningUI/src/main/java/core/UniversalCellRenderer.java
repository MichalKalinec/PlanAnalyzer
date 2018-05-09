package core;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.math.BigDecimal;
import java.math.RoundingMode;
import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Michal Kalinec 444505
 */
public class UniversalCellRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table,
            Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component parent = super.getTableCellRendererComponent(table,
                value, isSelected, hasFocus, row, column);
        if (table.getModel() instanceof OverviewMatrixTableModel) {
            if (row == table.getRowCount() - 1 || column == table.getColumnCount() - 1) {
                setFont(parent.getFont().deriveFont(Font.BOLD));
            }
        } else {
            CurrentPlanTableModel model = (CurrentPlanTableModel) table.getModel();
            if (value instanceof Double) {
                value = round((double) value, 2);
                setValue(value);
            }
            int font = 0;
            if (model.getOps().getOperations().get(model.getOpForRow(row)) != null) {
                font += Font.BOLD;
            }
            if (model.getOpForRow(table.convertRowIndexToModel(row)).isManuallyEnded()) {
                font += Font.ITALIC;
            }
            if (row > 0) {
                if (!model.getOpForRow(table.convertRowIndexToModel(row)).getOrderNo().equals(model.getOpForRow(table.convertRowIndexToModel(row - 1)).getOrderNo())) {
                    setBorder(BorderFactory.createMatteBorder(3, 0, 0, 0, Color.BLACK));
                }
            }
            setFont(parent.getFont().deriveFont(font));
            //setCellColor(table, row, column);
        }
        return this;
    }

    private void setCellColor(JTable table, int row, int column) {
        CurrentPlanTableModel model = (CurrentPlanTableModel) table.getModel();
        if (model.getOpForRow(table.convertRowIndexToModel(row)).isFinished()) {
            this.setBackground(Color.red);
        } else {
            this.setBackground(Color.white);
        }
        if (model.getValueAt(row, column) instanceof Double) {
            this.setBackground(Color.green);
        }
    }

    public static double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
