package core;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Basic class for representing operations.
 *
 * @author Michal Kalinec 444505
 */
public class Operation {

    private String orderNo;
    private String opNo;
    private String opDescription;
    private String itemDescription;
    private LocalDateTime startPlan;
    private LocalDateTime endPlan;
    private LocalDateTime startReal;
    private LocalDateTime endReal;
    private LocalDateTime endRescheduled;
    private LocalDateTime endRequired;
    private double quantityPlan;
    private double quantityReal;
    private double quantityTotal;
    private String workcen;
    private String delstore;
    private boolean manuallyEnded;
    private String itemNo;
    
    public boolean isFinished(){
        return quantityReal >= quantityTotal;
    }

    public LocalDateTime getEndRequired() {
        return endRequired;
    }

    public void setEndRequired(LocalDateTime endRequired) {
        this.endRequired = endRequired;
    }

    public boolean isManuallyEnded() {
        return manuallyEnded;
    }

    public void setIsManuallyEnded(boolean isManuallyEnded) {
        this.manuallyEnded = isManuallyEnded;
    }

    public String getDelstore() {
        return delstore;
    }

    public void setDelstore(String delstore) {
        this.delstore = delstore;
    }

    public LocalDateTime getStartReal() {
        return startReal;
    }

    public void setStartReal(LocalDateTime startReal) {
        this.startReal = startReal;
    }

    public LocalDateTime getEndRescheduled() {
        return endRescheduled;
    }

    public void setEndRescheduled(LocalDateTime endRescheduled) {
        this.endRescheduled = endRescheduled;
    }

    public double getQuantityTotal() {
        return quantityTotal;
    }

    public void setQuantityTotal(double quantityTotal) {
        this.quantityTotal = quantityTotal;
    }

    public LocalDateTime getStartPlan() {
        return startPlan;
    }

    public void setStartPlan(LocalDateTime startPlan) {
        this.startPlan = startPlan;
    }

    public double getQuantityReal() {
        return quantityReal;
    }

    public void setQuantityReal(double quantityReal) {
        this.quantityReal = quantityReal;
    }

    public double getQuantityPlan() {
        return quantityPlan;
    }

    public void setQuantityPlan(double quantityPlan) {
        this.quantityPlan = quantityPlan;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public String getOpDescription() {
        return opDescription;
    }

    public void setOpDescription(String opDescription) {
        this.opDescription = opDescription;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public void setOpNo(String opNo) {
        this.opNo = opNo;
    }
    
    public void setWorkcen(String workcen) {
        this.workcen = workcen;
    }

    public void setEndPlan(LocalDateTime endPlan) {
        this.endPlan = endPlan;
    }

    public LocalDateTime getEndReal() {
        return endReal;
    }

    public void setEndReal(LocalDateTime endReal) {
        this.endReal = endReal;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public String getOpNo() {
        return opNo;
    }

    public String getWorkcen() {
        return workcen;
    }

    public LocalDateTime getEndPlan() {
        return endPlan;
    }

    public String getItemNo() {
        return itemNo;
    }

    public void setItemNo(String itemNo) {
        this.itemNo = itemNo;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + Objects.hashCode(this.orderNo);
        hash = 17 * hash + Objects.hashCode(this.opNo);
        return hash;
    }

    //Equals based on orderNo and opNo.
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
        final Operation other = (Operation) obj;
        if (!Objects.equals(this.orderNo, other.orderNo)) {
            return false;
        }
        if (!Objects.equals(this.opNo, other.opNo)) {
            return false;
        }
        if (other.orderNo == null && other.opNo == null) {
            return false;
        }
        return true;
    }
}
