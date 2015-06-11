package ch.swissbytes.procurement.boundary.purchaseOrder;

import ch.swissbytes.Service.business.project.ProjectService;
import ch.swissbytes.Service.business.purchase.PurchaseOrderService;
import ch.swissbytes.domain.model.entities.ProjectEntity;
import ch.swissbytes.domain.model.entities.PurchaseOrderEntity;
import ch.swissbytes.domain.types.POStatusEnum;
import org.apache.commons.lang.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by alvaro on 6/1/2015.
 */

@Named
@ViewScoped
public class PoListBean implements Serializable {

    private static final Logger log = Logger.getLogger(PoListBean.class.getName());

    private List<PurchaseOrderEntity> list;

    @Inject
    private PurchaseOrderService service;

    @Inject
    private ProjectService projectService;

    private String projectId;

    private ProjectEntity project;

    private PurchaseOrderEntity currentPurchaseOrder;


    @PostConstruct
    public void create() {
        log.info("Created POListBean");
        list = new ArrayList<>();
        currentPurchaseOrder = new PurchaseOrderEntity();
    }

    public void load() {
        if (StringUtils.isNotEmpty(projectId) && StringUtils.isNotBlank(projectId)) {
            try {
                Long.parseLong(projectId);
            } catch (NumberFormatException nfe) {
                throw new IllegalArgumentException("project Id invalid");
            }
            project = projectService.findProjectById(Long.parseLong(projectId));
            if (project == null) {
                throw new IllegalArgumentException("project Id invalid");
            }
            list = service.purchaseListByProject(Long.parseLong(projectId));
        } else {
            throw new IllegalArgumentException("project Id invalid");
        }

    }

    @PreDestroy
    public void destroy() {
        log.info("Destroyed POListBean");
    }

    public void doCommitPo(){
        log.info("do commit purchase order");
        currentPurchaseOrder.getPoEntity().setPoProcStatus(POStatusEnum.COMMITED);
        currentPurchaseOrder=service.updateOnlyPOOnProcurement(currentPurchaseOrder);
    }

    public void doReleasePo(){
        log.info("do release purchase order");
        currentPurchaseOrder.getPoEntity().setPoProcStatus(POStatusEnum.READY);
        currentPurchaseOrder=service.updateOnlyPOOnProcurement((currentPurchaseOrder));
    }

    public boolean actionViewPOO(PurchaseOrderEntity entity){
        if ((entity.getPoEntity().getPoProcStatus().ordinal() == POStatusEnum.COMMITED.ordinal())
                || (entity.getPoEntity().getPoProcStatus().ordinal() == POStatusEnum.FINAL.ordinal())){
            return true;
        }
            return false;
    }

    public boolean actionEditPOO(PurchaseOrderEntity entity){
        if ((entity.getPoEntity().getPoProcStatus().ordinal() == POStatusEnum.READY.ordinal())
                || (entity.getPoEntity().getPoProcStatus().ordinal() == POStatusEnum.ON_HOLD.ordinal())){
            return true;
        }
        return false;
    }

    public boolean actionVarationPOO(PurchaseOrderEntity entity){
        if (entity.getPoEntity().getPoProcStatus().ordinal() == POStatusEnum.COMMITED.ordinal()){
            return true;
        }
        return false;
    }

    public boolean actionCommitPOO(PurchaseOrderEntity entity){
        if ((entity.getPoEntity().getPoProcStatus().ordinal() == POStatusEnum.READY.ordinal())
                || (entity.getPoEntity().getPoProcStatus().ordinal() == POStatusEnum.FINAL.ordinal())){
            return true;
        }
        return false;
    }

    public boolean actionReleasePOO(PurchaseOrderEntity entity){
        if (entity.getPoEntity().getPoProcStatus().ordinal() == POStatusEnum.FINAL.ordinal()){
            return true;
        }
        return false;
    }

    public boolean actionPrintPOO(PurchaseOrderEntity entity){
        if ((entity.getPoEntity().getPoProcStatus().ordinal() == POStatusEnum.READY.ordinal())
                || (entity.getPoEntity().getPoProcStatus().ordinal() == POStatusEnum.FINAL.ordinal())
                || (entity.getPoEntity().getPoProcStatus().ordinal() == POStatusEnum.COMMITED.ordinal())){
            return true;
        }
        return false;
    }

    public List<PurchaseOrderEntity> getList() {
        return list;
    }

    public ProjectEntity getProject() {
        return project;
    }

    public void setProject(ProjectEntity project) {
        this.project = project;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public PurchaseOrderEntity getCurrentPurchaseOrder() {
        return currentPurchaseOrder;
    }

    public void setCurrentPurchaseOrder(PurchaseOrderEntity currentPurchaseOrder) {
        this.currentPurchaseOrder = currentPurchaseOrder;
    }
}
