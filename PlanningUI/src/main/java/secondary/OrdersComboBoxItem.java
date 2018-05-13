package secondary;

import core.Operation;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author Michal Kalinec 444505
 */
public class OrdersComboBoxItem implements Comparable {

    private String orderNo;
    private String itemNo;
    private String itemDesc;

    public OrdersComboBoxItem(Operation operation) {
        this.orderNo = operation.getOrderNo();
        if (operation.getItemNo() == null) {
            itemNo = "<Ø>";
        } else {
            itemNo = operation.getItemNo();
        }
        if (operation.getItemDescription() == null) {
            itemDesc = "<žiadny popis>";
        } else {
            itemDesc = operation.getItemDescription();
        }
    }
    
    public static List<OrdersComboBoxItem> createAsList (Set<Operation> ops) {
        List<OrdersComboBoxItem> result = new ArrayList<>();
        for (Operation op : ops) {
            OrdersComboBoxItem tmp = new OrdersComboBoxItem(op);
            if (!result.contains(tmp)){
                result.add(tmp);
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return itemNo + "-" + itemDesc;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public String getItemNo() {
        return itemNo;
    }

    public String getItemDesc() {
        return itemDesc;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.orderNo);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final OrdersComboBoxItem other = (OrdersComboBoxItem) obj;
        if (!Objects.equals(this.orderNo, other.orderNo)) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(Object o) {
        if (!(o instanceof OrdersComboBoxItem)) {
            return 0;
        }
        String other = ((OrdersComboBoxItem) o).getItemNo();
        return -itemNo.compareTo(other);
    }

}
