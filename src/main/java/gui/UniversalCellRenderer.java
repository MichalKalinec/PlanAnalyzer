package gui;

import backend_core.Operation;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.time.LocalDateTime;
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
            if (table.getModel().getValueAt(table.convertRowIndexToModel(row), 0).equals("Súčet") || column == table.getColumnCount() - 1) {
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
            setFont(parent.getFont().deriveFont(font));
            if (row > 0) {
                if (op.getItemNo() == null) {
                    return this;
                }
                if (!table.getRowSorter().getSortKeys().isEmpty()) {
                    if (table.getRowSorter().getSortKeys().get(0).getColumn() != 1) {
                        return this;
                    }
                }
                if (!op.getItemNo().equals(model.getOpForRow(table.convertRowIndexToModel(row - 1)).getItemNo()) && thickLines) {
                    setBorder(BorderFactory.createMatteBorder(3, 0, 0, 0, Color.BLACK));
                }
            }
        }
        return this;
    }

/*    private void setCellColor(JTable table, int row, int column) {
        CurrentPlanTableModel model = (CurrentPlanTableModel) table.getModel();
        if (!model.getOpForRow(table.convertRowIndexToModel(row)).isFinished() &&
                LocalDateTime.now().isAfter(model.getOpForRow(table.convertRowIndexToModel(row)).getEndRequired())) {
            this.setBackground(new Color(200, 0, 0));
        } else {
            this.setBackground(Color.white);
        }
    }
*/
    public void setThickLines(boolean thickLines) {
        this.thickLines = thickLines;
    }
}
