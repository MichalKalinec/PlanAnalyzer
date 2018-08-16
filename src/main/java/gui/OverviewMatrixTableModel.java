package gui;

import backend_core.Note;
import backend_core.NotesManager;
import secondary.NoteCategoryClass;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author Michal Kalinec 444505
 */
public class OverviewMatrixTableModel extends AbstractTableModel {

    Map<String, int[]> counts;

    public void resize(JTable table) {
        table.setColumnSelectionAllowed(true);
        table.getColumnModel().getColumn(0).setPreferredWidth(200);
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(table.getModel());
        table.setRowSorter(sorter);
    }
    
    public void loadCounts(boolean notEmpty) throws SQLException {
        counts = (LinkedHashMap) NotesManager.sumNotesForWorkcens(notEmpty);
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
            return NoteCategoryClass.getDescWithCat(columnIndex);
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
            return counts.keySet().toArray()[rowIndex] == null ? "" : counts.keySet().toArray()[rowIndex];
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
    
    public String getNotesSummary(List<List<Object>> notes) throws SQLException {
        String s = "";
        for (int i = 0; i < notes.size(); i++) {
            if (i > 0) {
                s = s.concat("---------------------------------------------------------------------------------------------------------" + System.getProperty("line.separator"));
            }
            s = s.concat(notes.get(i).get(0) + "; " + notes.get(i).get(1) + System.getProperty("line.separator")
                    + "Kategória: " + NoteCategoryClass.getDescWithCat(((Note) notes.get(i).get(2)).getCategory())
                    + System.getProperty("line.separator") + System.getProperty("line.separator")
                    + "Popis: " + ((Note) notes.get(i).get(2)).getText() + System.getProperty("line.separator"));
        }
        return s;
    }
}
