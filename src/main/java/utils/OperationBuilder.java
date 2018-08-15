package utils;

import backend_core.Operation;
import java.time.LocalDateTime;

/**
 * Class for easy creation of operations.
 * @author Michal Kalinec 444505
 */
public class OperationBuilder {
    private String orderNo;
    private String opNo;
    private String workcen;
    private String delstore;
    private LocalDateTime startPlan;
    private LocalDateTime endPlan;
    private LocalDateTime startReal;
    private LocalDateTime endReal;
    private LocalDateTime endRescheduled;
    private LocalDateTime endRequired;
    private double quantityPlan;
    private double quantityReal;
    private double quantityTotal;
    private double quantityRequired;
    private String itemDescription;
    private String opDescription;
    private boolean manuallyEnded;
    private String itemNo;
    
    public OperationBuilder orderNo(String orderNo){
        this.orderNo = orderNo;
        return this;
    }
    
    public OperationBuilder opNo(String opNo){
        this.opNo = opNo;
        return this;
    }
    
    public OperationBuilder workcen(String workcen){
        if (workcen != null) {
            this.workcen = workcen;
        }
        return this;
    }
    
    public OperationBuilder delstore(String delstore){
        if (delstore != null) {
            this.delstore = delstore.replaceAll("\\s+","");
        }
        return this;
    }
    
    public OperationBuilder startPlan(LocalDateTime startPlan){
        this.startPlan = startPlan;
        return this;
    }
    
    public OperationBuilder endPlan(LocalDateTime endPlan){
        this.endPlan = endPlan;
        return this;
    }
    
    public OperationBuilder startReal(LocalDateTime startReal){
        this.startReal = startReal;
        return this;
    }
    
    public OperationBuilder endReal(LocalDateTime endReal){
        this.endReal = endReal;
        return this;
    }
    
    public OperationBuilder endRescheduled(LocalDateTime endRescheduled){
        this.endRescheduled = endRescheduled;
        return this;
    }
    
    public OperationBuilder endRequired(LocalDateTime endRequired){
        this.endRequired = endRequired;
        return this;
    }
    
    public OperationBuilder itemDescription(String itemDescription){
        if (itemDescription != null) {
            this.itemDescription = itemDescription;
        }
        return this;
    }
    
    public OperationBuilder opDescription(String opDescription){
        if (opDescription != null) {
            this.opDescription = opDescription;
        }
        return this;
    }
    
    public OperationBuilder quantityPlan(double quantityPlan){
        this.quantityPlan = quantityPlan;
        return this;
    }
    
    public OperationBuilder quantityTotal(double quantityTotal){
        this.quantityTotal = quantityTotal;
        return this;
    }
    
    public OperationBuilder quantityReal(double quantityReal){
        this.quantityReal = quantityReal;
        return this;
    }
    
    public OperationBuilder quantityRequired(double quantityRequired){
        this.quantityRequired = quantityRequired;
        return this;
    }
    
    public OperationBuilder manuallyEnded(boolean boo){
        this.manuallyEnded = boo;
        return this;
    }
    
    public OperationBuilder itemNo(String itemNo){
        this.itemNo = itemNo;
        return this;
    }
    
    public Operation build(){
        Operation operation = new Operation();
        operation.setOrderNo(orderNo);
        operation.setOpNo(opNo);
        operation.setOpDescription(opDescription);
        operation.setItemDescription(itemDescription);
        operation.setQuantityPlan(quantityPlan);
        operation.setStartPlan(startPlan);
        operation.setEndPlan(endPlan);
        operation.setEndReal(endReal);
        operation.setQuantityReal(quantityReal);
        operation.setQuantityTotal(quantityTotal);
        operation.setWorkcen(workcen);
        operation.setDelstore(delstore);
        operation.setEndRescheduled(endRescheduled);
        operation.setStartReal(startReal);
        operation.setIsManuallyEnded(manuallyEnded);
        operation.setItemNo(itemNo);
        operation.setEndRequired(endRequired);
        operation.setQuantityRequired(quantityRequired);
        return operation;
    }
}
