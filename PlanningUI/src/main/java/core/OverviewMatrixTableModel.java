package core;

import secondary.NoteCategoryClass;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import utils.DBUtils;

/**
 *
 * @author Michal Kalinec 444505
 */
public class OverviewMatrixTableModel extends AbstractTableModel {

    Map<String, int[]> counts;

    public OverviewMatrixTableModel(boolean notEmpty) throws SQLException {
        counts = (LinkedHashMap) DBUtils.sumNotesForWorkcens(notEmpty);
    }

    public void resize(JTable table) {
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(0).setPreferredWidth(200);
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(table.getModel());
        table.setRowSorter(sorter);
    }

    @Override
    public int getRowCount() {
        return counts.size() + 1;
    }

    @Override
    public int getColumnCount() {
        return 14;
    }

    @Override
    public String getColumnName(int columnIndex) {
        if (columnIndex == 0) {
            return "Stredisko";
        }
        if (columnIndex < getColumnCount() - 1) {
            return NoteCategoryClass.getShortDesc(columnIndex);
        }
        if (columnIndex == getColumnCount() - 1) {
            return "Súčet";
        }
        throw new IllegalArgumentException("columnIndex " + columnIndex);
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == 0) {
            return String.class;
        }
        if (columnIndex < getColumnCount()) {
            return Integer.class;
        }
        throw new IllegalArgumentException("columnIndex");
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            if (rowIndex == counts.size()) {
                return "Súčet";
            }
            return counts.keySet().toArray()[rowIndex];
        }
        if (columnIndex == getColumnCount() - 1) {
            int sum = 0;
            for (int i = 1; i < columnIndex; i++) {
                if (getValueAt(rowIndex, i) != null) {
                    sum += (int) getValueAt(rowIndex, i);
                }
            }
            return sum;
        }
        if (rowIndex == counts.size()) {
            int sum = 0;
            for (String workcen : counts.keySet()) {
                sum += counts.get(workcen)[columnIndex - 1];
            }
            return sum;
        }
        if (counts.get(counts.keySet().toArray()[rowIndex])[columnIndex - 1] == 0) {
            return null;
        }
        return counts.get(counts.keySet().toArray()[rowIndex])[columnIndex - 1];
    }
}
