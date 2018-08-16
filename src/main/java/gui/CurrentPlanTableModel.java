package gui;

import backend_core.Operation;
import backend_core.OperationsMap;
import java.time.LocalDate;
import javax.swing.table.AbstractTableModel;
import static java.time.temporal.ChronoUnit.DAYS;
import java.math.BigDecimal;
import java.math.RoundingMode;
import static java.lang.Math.abs;
import secondary.NoteCategoryClass;

/**
 *
 * @author Michal Kalinec 444505
 */
public class CurrentPlanTableModel extends AbstractTableModel {

    private OperationsMap ops;
    
    public CurrentPlanTableModel(){};

    @Override
    public int getColumnCount() {
        return 16;
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
            case 15:
                return "Zákazkový list";
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
            case 15:
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
                if(op.getEndRescheduled() == null) {
                    return null;
                }
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
                return round(op.getQuantityPlan(), 2);
            case 13:
                return round(op.getQuantityReal(), 2);
            case 14:
                return round(op.getQuantityTotal(), 2);
            case 15:
                return round(op.getQuantityRequired(), 2);
            default:
                throw new IllegalArgumentException("columnIndex: " + columnIndex);
        }
    }
    
    public String getNotesSummary(int index) {
        String s = "";
        for (int i = 0; i < ops.getOperations().get(getOpForRow(index)).size(); i++) {
            if (i > 0) {
                s = s.concat("---------------------------------------------------------------------------------------------------------" + System.getProperty("line.separator"));
            }
            s = s.concat("Kategória: " + NoteCategoryClass.getDescWithCat(ops.getOperations().get(getOpForRow(index)).get(i).getCategory())
                    + System.getProperty("line.separator") + System.getProperty("line.separator")
                    + "Popis: " + ops.getOperations().get(getOpForRow(index)).get(i).getText() + System.getProperty("line.separator"));
        }
        return s;
    }

    public Operation getOpForRow(int row) {
        return (Operation) ops.getOperations().keySet().toArray()[row];
    }

    public OperationsMap getOps() {
        return ops;
    }

    public void setOps(OperationsMap ops) {
        this.ops = ops;
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
