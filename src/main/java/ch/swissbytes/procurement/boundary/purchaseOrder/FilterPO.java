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

    public boolean isP0000Selected(){
        return purchaseOrderNumberOption!=null&&purchaseOrderNumberOption.ordinal()==PurchaseOrderNumberOption.P0000.ordinal();
    }
    public boolean isP1000Selected(){
        return purchaseOrderNumberOption!=null&&purchaseOrderNumberOption.ordinal()==PurchaseOrderNumberOption.P1000.ordinal();
    }
    public boolean isP2000Selected(){
        return purchaseOrderNumberOption!=null&&purchaseOrderNumberOption.ordinal()==PurchaseOrderNumberOption.P2000.ordinal();
    }
    public boolean isP3000Selected(){
        return purchaseOrderNumberOption!=null&&purchaseOrderNumberOption.ordinal()==PurchaseOrderNumberOption.P3000.ordinal();
    }
    public boolean isP4000Selected(){
        return purchaseOrderNumberOption!=null&&purchaseOrderNumberOption.ordinal()==PurchaseOrderNumberOption.P4000.ordinal();
    }
    public boolean isP5000Selected(){
        return purchaseOrderNumberOption!=null&&purchaseOrderNumberOption.ordinal()==PurchaseOrderNumberOption.P5000.ordinal();
    }
    public boolean isP6000Selected(){
        return purchaseOrderNumberOption!=null&&purchaseOrderNumberOption.ordinal()==PurchaseOrderNumberOption.P6000.ordinal();
    }
    public boolean isP7000Selected(){
        return purchaseOrderNumberOption!=null&&purchaseOrderNumberOption.ordinal()==PurchaseOrderNumberOption.P7000.ordinal();
    }
    public boolean isP8000Selected(){
        return purchaseOrderNumberOption!=null&&purchaseOrderNumberOption.ordinal()==PurchaseOrderNumberOption.P8000.ordinal();
    }
    public boolean isP9000Selected(){
        return purchaseOrderNumberOption!=null&&purchaseOrderNumberOption.ordinal()==PurchaseOrderNumberOption.P9000.ordinal();
    }
    public boolean isSCSelected(){
        return classEnum!=null&&classEnum.ordinal()==ClassEnum.SERVICE_CONTRACT.ordinal();
    }
    public boolean isCCSelected(){
        return classEnum!=null&&classEnum.ordinal()==ClassEnum.CONSTRUCTION_CONTRACT.ordinal();
    }
    public void clear(){
        super.clean();
        projectId=null;
    }
}
