package ch.swissbytes.procurement.boundary.purchaseOrder;

import ch.swissbytes.Service.business.purchase.PurchaseOrderService;
import ch.swissbytes.domain.model.entities.PurchaseOrderEntity;
import ch.swissbytes.procurement.boundary.Bean;

import javax.faces.bean.ViewScoped;
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

    @Override
    protected void initialize() {
        purchaseOrder=new PurchaseOrderEntity();
    }
    @Override
    protected  void ending(){
        //sub class should implement something
    }

    public void doSave(){
        service.savePOOnProcurement(purchaseOrder);
    }
    public void doUpdate(){
        service.updatePOOnProcurement(purchaseOrder);
    }
}
