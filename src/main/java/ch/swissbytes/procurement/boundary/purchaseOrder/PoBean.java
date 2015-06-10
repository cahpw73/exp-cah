package ch.swissbytes.procurement.boundary.purchaseOrder;

import ch.swissbytes.Service.business.project.ProjectService;
import ch.swissbytes.Service.business.purchase.PurchaseOrderService;
import ch.swissbytes.domain.model.entities.*;
import ch.swissbytes.domain.types.POStatusEnum;
import ch.swissbytes.procurement.boundary.Bean;
import org.apache.commons.lang.StringUtils;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.logging.Logger;


/**
 * Created by alvaro on 6/1/2015.
 */

@Named
@ViewScoped
public class PoBean extends Bean {

    private static final Logger log = Logger.getLogger(PoBean.class.getName());

    private PurchaseOrderEntity purchaseOrder;

    private String projectId;

    private String poId;

    private Boolean modeView;

    @Inject
    private PurchaseOrderService service;

    @Inject
    private ProjectService projectService;

    @Inject
    private ItemBean itemBean;

    @Inject
    private RequisitionBean requisitionBean;

    @Inject
    private DeliverableBean deliverableBean;

    @Inject
    private PoTextBean poTextBean;

    @Inject
    private CashflowBean cashflowBean;

    public ItemBean getItemBean() {
        return itemBean;
    }

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
                itemBean.loadItemList(purchaseOrder.getPoEntity().getId());
                cashflowBean.loadCashflow(purchaseOrder.getPoEntity().getId());
                if(purchaseOrder==null){
                    throw new IllegalArgumentException("invalid purchase order Id");
                }
                requisitionBean.getList().addAll(purchaseOrder.getPoEntity().getRequisitions());
                deliverableBean.getList().addAll(purchaseOrder.getPoEntity().getDeliverables());
                if(modeView == null) {
                    log.info("mode edition");
                    putModeEdition();
                }else if(modeView) {
                    log.info("mode view");
                    putModeView();
                }

            }catch(NumberFormatException nfe){
                throw new IllegalArgumentException("invalid purchase order Id");
            }
        }else{
            throw new IllegalArgumentException("parameter missing");
        }
        poTextBean.loadProjectTextSnippets(purchaseOrder.getProjectEntity().getId());
    }

    @Override
    protected void initialize() {
        purchaseOrder=new PurchaseOrderEntity();
    }


    public String doSave(){
        log.info("trying to save purchase order on procurement module");
        collectData();
        purchaseOrder.getPoEntity().setPoProcStatus(POStatusEnum.READY);
        purchaseOrder=service.savePOOnProcurement(purchaseOrder);
        log.info("purchase order created ["+purchaseOrder.getId()+"]");
        return backToList();
    }

    public String doUpdate() {
        log.info("trying to update purchase order on procurement module");
        collectData();
        purchaseOrder.getPoEntity().setPoProcStatus(POStatusEnum.READY);
        purchaseOrder=service.updatePOOnProcurement(purchaseOrder);
        log.info("purchase order updated [" + purchaseOrder.getId()+"]");
        return backToList();
    }

    public String doSaveView(){
        log.info("trying to saveView purchase order on procurement module");
        collectData();
        purchaseOrder=service.savePOOnProcurement(purchaseOrder);
        log.info("purchase order created ["+purchaseOrder.getId()+"]");
        return backToList();
    }

    public String doUpdateView(){
        log.info("trying to updateView purchase order on procurement module");
        collectData();
        purchaseOrder=service.updatePOOnProcurement(purchaseOrder);
        log.info("purchase order created ["+purchaseOrder.getId()+"]");
        return backToList();
    }

    public String backToList(){
        return "list.xhtml?faces-redirect=true&projectId="+purchaseOrder.getProjectEntity().getId();
    }

    public String poStatusProc(){
        if(purchaseOrder.getPoEntity().getPoProcStatus() != null)
            return purchaseOrder.getPoEntity().getPoProcStatus().name();
        else
            return "";

    }

    private void collectData(){
        purchaseOrder.getPoEntity().getItemList().addAll(itemBean.getItemList());
        purchaseOrder.getPoEntity().getRequisitions().addAll(requisitionBean.getList());
        purchaseOrder.getPoEntity().getDeliverables().addAll(deliverableBean.getList());
        purchaseOrder.getPoEntity().setCashflow(cashflowBean.getCashflow());
        purchaseOrder.getPoEntity().getCashflow().getCashflowDetailList().addAll(cashflowBean.getCashflowDetailList());
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

    public Boolean getModeView() {
        return modeView;
    }

    public void setModeView(Boolean modeView) {
        this.modeView = modeView;
    }
}
