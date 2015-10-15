package ch.swissbytes.procurement.boundary.purchaseOrder;

import ch.swissbytes.Service.infrastructure.Filter;
import ch.swissbytes.domain.types.ClassEnum;
import ch.swissbytes.domain.types.PurchaseOrderNumberOption;

/**
 * Created by alvaro on 8/4/2015.
 */
public class FilterPO extends Filter {

    private Long projectId;
    private PurchaseOrderNumberOption purchaseOrderNumberOption;
    private ClassEnum classEnum;

    public ClassEnum getClassEnum() {
        return classEnum;
    }

    public void setClassEnum(ClassEnum classEnum) {
        this.classEnum = classEnum;
    }

    public PurchaseOrderNumberOption getPurchaseOrderNumberOption() {
        return purchaseOrderNumberOption;
    }

    public void setPurchaseOrderNumberOption(PurchaseOrderNumberOption purchaseOrderNumberOption) {
        this.purchaseOrderNumberOption = purchaseOrderNumberOption;
    }

    public Long getProjectId() {
        return projectId;
    }



    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public void setPOOO(){
        purchaseOrderNumberOption=PurchaseOrderNumberOption.P0000;
        classEnum=null;
    }
    public void setP1000(){
        purchaseOrderNumberOption=PurchaseOrderNumberOption.P1000;
        classEnum=null;
    }
    public void setP2000(){
        purchaseOrderNumberOption=PurchaseOrderNumberOption.P2000;
        classEnum=null;
    }
    public void setP3000(){
        purchaseOrderNumberOption=PurchaseOrderNumberOption.P3000;
        classEnum=null;
    }
    public void setP4000(){
        purchaseOrderNumberOption=PurchaseOrderNumberOption.P4000;
        classEnum=null;
    }
    public void setP5000(){
        purchaseOrderNumberOption=PurchaseOrderNumberOption.P5000;
        classEnum=null;
    }
    public void setP6000(){
        purchaseOrderNumberOption=PurchaseOrderNumberOption.P6000;
        classEnum=null;
    }
    public void setP7000(){
        purchaseOrderNumberOption=PurchaseOrderNumberOption.P7000;
        classEnum=null;
    }
    public void setP8000(){
        purchaseOrderNumberOption=PurchaseOrderNumberOption.P8000;
        classEnum=null;
    }
    public void setP9000(){
        purchaseOrderNumberOption=PurchaseOrderNumberOption.P9000;
        classEnum=null;
    }
    public void setCC(){
        purchaseOrderNumberOption=null;
        classEnum=ClassEnum.CONSTRUCTION_CONTRACT;
    }
    public void setSC(){
        purchaseOrderNumberOption=null;
        classEnum=ClassEnum.SERVICE_CONTRACT;
    }
    public void clear(){
        super.clean();
        projectId=null;
    }
}
