package thesis.planningui;

import java.util.Objects;

/**
 *
 * @author Michal Kalinec 444505
 */
class OrdersComboBoxItem {

    private String orderNo;
    private String itemNo;
    private String itemDesc;

    public OrdersComboBoxItem(String orderNo, String itemNo, String itemDesc) {
        this.orderNo = orderNo;
        if (itemNo == null) {
            this.itemNo = "<Ø>";
        } else {
            this.itemNo = itemNo;
        }
        if (itemDesc == null) {
            this.itemDesc = "<žiadny popis>";
        } else {
            this.itemDesc = itemDesc;
        }
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

}
