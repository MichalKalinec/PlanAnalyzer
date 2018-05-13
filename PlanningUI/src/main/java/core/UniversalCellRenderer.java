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
    private boolean thickLines;

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
            Operation op = model.getOpForRow(table.convertRowIndexToModel(row));
            int font = 0;
            if (model.getOps().getOperations().get(op) != null) {
                font += Font.BOLD;
            }
            if (op.isManuallyEnded()) {
                font += Font.ITALIC;
            }
            if (row > 0) {
                if(op.getItemNo() == null ){
                    return this;
                }
                if (!op.getItemNo().equals(model.getOpForRow(table.convertRowIndexToModel(row - 1)).getItemNo()) && thickLines) {
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
    
    public void setThickLines(boolean thickLines) {
        this.thickLines = thickLines;
    }
}
