package ch.swissbytes.procurement.boundary.purchaseOrder;

import ch.swissbytes.Service.business.project.ProjectService;
import ch.swissbytes.Service.business.purchase.PurchaseOrderService;
import ch.swissbytes.domain.model.entities.POEntity;
import ch.swissbytes.domain.model.entities.ProjectEntity;
import ch.swissbytes.domain.model.entities.PurchaseOrderEntity;
import ch.swissbytes.procurement.boundary.Bean;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;


/**
 * Created by alvaro on 6/1/2015.
 */

@Named
@ViewScoped
public class PoBean extends Bean {


    private PurchaseOrderEntity purchaseOrder;

    private String projectId;

    private String poId;

    @Inject
    private PurchaseOrderService service;

    @Inject
    private ProjectService projectService;



    public void load(){
        if(projectId!=null){
            try {
                ProjectEntity projectEntity = projectService.findProjectById(Long.parseLong(projectId));
                if (projectEntity != null) {
                    purchaseOrder.setProjectEntity(projectEntity);
                    purchaseOrder.setProject(projectEntity.getProjectNumber());
                    purchaseOrder.setPoEntity(new POEntity());
                    putModeCreation();
                }else{
                    throw new IllegalArgumentException(" project invalid");
                }
            }catch (NumberFormatException nfe){
                throw new IllegalArgumentException("parameter project invalid");
            }
        }else if(poId!=null){
            try {
                purchaseOrder=service.findById(Long.valueOf(poId));
                if(purchaseOrder==null){
                    throw new IllegalArgumentException("invalid purchase order Id");
                }
            }catch(NumberFormatException nfe){
                throw new IllegalArgumentException("invalid purchase order Id");
            }
        }else{
            throw new IllegalArgumentException("parameter missing");
        }

    }

    @Override
    protected void initialize() {
        purchaseOrder=new PurchaseOrderEntity();
    }


    public String doSave(){
        service.savePOOnProcurement(purchaseOrder);
        return "list?projectId="+purchaseOrder.getProjectEntity().getId();
    }
    public String doUpdate(){
        service.updatePOOnProcurement(purchaseOrder);
        return "list?projectId="+purchaseOrder.getProjectEntity().getId();
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getPoId() {
        return poId;
    }

    public void setPoId(String poId) {
        this.poId = poId;
    }

    public PurchaseOrderEntity getPurchaseOrder() {
        return purchaseOrder;
    }

    public void setPurchaseOrder(PurchaseOrderEntity purchaseOrder) {
        this.purchaseOrder = purchaseOrder;
    }
}
