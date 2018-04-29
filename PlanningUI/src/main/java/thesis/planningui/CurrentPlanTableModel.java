package thesis.planningui;

import core.*;
import java.time.LocalDate;
import javax.swing.table.AbstractTableModel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import java.util.Comparator;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import static java.lang.Math.abs;
import static java.time.temporal.ChronoUnit.DAYS;

/**
 *
 * @author Michal Kalinec 444505
 */
public class CurrentPlanTableModel extends AbstractTableModel {

    private OperationsList ops;
    
    public CurrentPlanTableModel(){};

    public CurrentPlanTableModel(OperationsList ops) {
        this.ops = ops;
    }

    public void resize(JTable table) {
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(1).setPreferredWidth(120);
        table.getColumnModel().getColumn(2).setPreferredWidth(200);
        table.getColumnModel().getColumn(4).setPreferredWidth(200);
        table.getColumnModel().getColumn(5).setPreferredWidth(200);

        TableRowSorter<TableModel> sorter = new TableRowSorter<>(table.getModel());
        sorter.setComparator(11, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                Integer v1 = Integer.parseInt(o1.split(" ")[0]);
                Integer v2 = Integer.parseInt(o2.split(" ")[0]);
                return v1.compareTo(v2);
            }
        });
        table.setRowSorter(sorter);
    }

    @Override
    public int getColumnCount() {
        return 15;
    }

    @Override
    public int getRowCount() {
        if (ops == null) {
            return 0;
        }
        return ops.getOperations().size();
    }

    @Override
    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return "Zákazka";
            case 1:
                return "Číslo položky";
            case 2:
                return "Popis výrobku";
            case 3:
                return "Operácia";
            case 4:
                return "Popis operácie";
            case 5:
                return "Pracovisko";
            case 6:
                return "Skutočný začiatok";
            case 7:
                return "Pôvodne plánovaný koniec";
            case 8:
                return "Naposledy plánovaný koniec";
            case 9:
                return "Skutočný koniec";
            case 10:
                return "Požadovaný koniec";
            case 11:
                return "Meškanie";
            case 12:
                return "Naplánované množstvo";
            case 13:
                return "Vyrobené množstvo";
            case 14:
                return "Celkové množstvo";
            default:
                throw new IllegalArgumentException("columnIndex " + columnIndex);
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex
    ) {
        switch (columnIndex) {
            case 0:
                return String.class;
            case 1:
                return String.class;
            case 2:
                return String.class;
            case 3:
                return String.class;
            case 4:
                return String.class;
            case 5:
                return String.class;
            case 6:
                return LocalDate.class;
            case 7:
                return LocalDate.class;
            case 8:
                return LocalDate.class;
            case 9:
                return LocalDate.class;
            case 10:
                return LocalDate.class;
            case 11:
                return String.class;
            case 12:
                return Double.class;
            case 13:
                return Double.class;
            case 14:
                return Double.class;
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex
    ) {
        if (rowIndex < 0 || rowIndex >= ops.getOperations().size()) {
            return 0;
        }
        Operation op = getOpForRow(rowIndex);
        switch (columnIndex) {
            case 0:
                return op.getOrderNo();
            case 1:
                return op.getItemNo();
            case 2:
                return op.getItemDescription();
            case 3:
                return op.getOpNo();
            case 4:
                return op.getOpDescription();
            case 5:
                return op.getWorkcen();
            case 6:
                return op.getStartReal() == null ? null : op.getStartReal().toLocalDate();
            case 7:
                return op.getEndPlan() == null ? null : op.getEndPlan().toLocalDate();
            case 8:
                return op.getEndRescheduled() == null ? null : op.getEndRescheduled().toLocalDate();
            case 9:
                return op.getEndReal() == null ? null : op.getEndReal().toLocalDate();
            case 10:
                return op.getEndRequired()== null ? null : op.getEndRequired().toLocalDate();
            case 11:
                int days;
                if (op.getEndReal() != null) {
                    days = (int) op.getEndRescheduled().until(op.getEndReal(), DAYS);
                } else {
                    days = (int) op.getEndRescheduled().until(LocalDate.now().atStartOfDay(), DAYS);
                    if (days < 0) {
                        return null;
                    }
                }
                if (abs(days) == 1) {
                    return days + " deň";
                }
                return days + " dní";
            case 12:
                return op.getQuantityPlan();
            case 13:
                return op.getQuantityReal();
            case 14:
                return op.getQuantityTotal();
            default:
                throw new IllegalArgumentException("columnIndex: " + columnIndex);
        }
    }

    public Operation getOpForRow(int row) {
        return (Operation) ops.getOperations().keySet().toArray()[row];
    }

    public OperationsList getOps() {
        return ops;
    }

    public void setOps(OperationsList ops) {
        this.ops = ops;
    }
}
