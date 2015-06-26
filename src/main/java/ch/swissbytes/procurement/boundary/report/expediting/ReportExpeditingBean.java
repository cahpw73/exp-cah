package ch.swissbytes.procurement.boundary.report.expediting;

import ch.swissbytes.Service.business.deliverable.DeliverableDao;
import ch.swissbytes.Service.business.item.ItemService;
import ch.swissbytes.Service.business.project.ProjectService;
import ch.swissbytes.Service.business.purchase.PurchaseOrderService;
import ch.swissbytes.domain.model.entities.DeliverableEntity;
import ch.swissbytes.domain.model.entities.ProjectEntity;
import ch.swissbytes.domain.model.entities.PurchaseOrderEntity;
import ch.swissbytes.domain.model.entities.ScopeSupplyEntity;
import ch.swissbytes.domain.types.POStatusEnum;
import ch.swissbytes.procurement.boundary.report.deliverable.DeliverableDto;
import ch.swissbytes.procurement.report.ReportProcBean;

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
 * Created by christian on 12-06-2015
 */
@Named
@ViewScoped
public class ReportExpeditingBean implements Serializable {

    public static final Logger log = Logger.getLogger(ReportExpeditingBean.class.getName());

    @Inject
    private ProjectService projectService;

    @Inject
    private PurchaseOrderService purchaseOrderService;

    @Inject
    private DeliverableDao deliverableDao;

    @Inject
    private ReportProcBean reportProcBean;

    @Inject
    private ItemService itemService;

    private List<ProjectEntity> projectList;

    private List<PurchaseOrderEntity> purchaseOrderList;

    private ProjectEntity selectedProject;

    private String termsPoNo;

    private List<ExpeditingDto> expeditingDtosList;

    @PostConstruct
    public void create() {
        log.info("creating reportDeliverableBean");
        selectedProject = new ProjectEntity();
        projectList = new ArrayList<>();
        purchaseOrderList = new ArrayList<>();
        expeditingDtosList = new ArrayList<>();
        loadProjects();
    }

    @PreDestroy
    public void destroy() {
        log.info("destroyed reportDeliverableBean");
    }

    public void loadProjects() {
        log.info("loading projects");
        projectList = projectService.findAllProjects();
    }

    public void searchPurchaseOrder() {
        log.info("searching purchase order");
        if (selectedProject != null) {
            loadExpeditingDtoList(selectedProject.getId(), null);
        } else {
            purchaseOrderList.clear();
            expeditingDtosList.clear();
        }
        termsPoNo = "";
    }

    public void filterExpeditingDtoListByPoNo() {
        log.info("filtering by project id and poNo");
        loadExpeditingDtoList(selectedProject.getId(), termsPoNo != null ? termsPoNo : "");

    }

    public void printReportExpediting(){
        log.info("printing report expediting");
        reportProcBean.printReportExpediting(purchaseOrderList.get(0), selectedProject.getId(), termsPoNo);
    }

    public String backToReports(){
        return "report/report?faces-redirect=true";
    }

    public void doSave(){
        log.info("saving changes to scope supply entity");
        for(ExpeditingDto dto : expeditingDtosList){
            ScopeSupplyEntity supplyEntity = itemService.findById(dto.getItemId());
            supplyEntity.setExcludeFromExpediting(dto.getExcludeFromExpediting());
            itemService.doUpdate(supplyEntity);
        }
    }

    public boolean hasStatusCommited(POStatusEnum status){
        return POStatusEnum.COMMITED.ordinal() == status.ordinal();
    }

    private void loadExpeditingDtoList(Long projectId, String poNo) {
        purchaseOrderList.clear();
        purchaseOrderList = purchaseOrderService.purchaseListByProjectIdAnPoNo(projectId, poNo);
        for (PurchaseOrderEntity p : purchaseOrderList) {
            p.getPoEntity().getScopeSupplyList().addAll(itemService.findByPoId(p.getId()));
        }
        expeditingDtosList.clear();
        for (PurchaseOrderEntity p : purchaseOrderList) {
            for(ScopeSupplyEntity s : p.getPoEntity().getScopeSupplyList()){
                ExpeditingDto dto = new ExpeditingDto(p,s);
                expeditingDtosList.add(dto);
            }
            log.info("POEntity id: " + p.getPoEntity().getId());
        }
    }

    public boolean isProjectSelected() {
        return selectedProject != null ? (selectedProject.getId() != null ? true : false) : false;
    }

    public ProjectEntity getSelectedProject() {
        return selectedProject;
    }

    public void setSelectedProject(ProjectEntity selectedProject) {
        this.selectedProject = selectedProject;
    }

    public List<ProjectEntity> getProjectList() {
        return projectList;
    }

    public List<ExpeditingDto> getExpeditingDtosList() {
        return expeditingDtosList;
    }

    public String getTermsPoNo() {
        return termsPoNo;
    }

    public void setTermsPoNo(String termsPoNo) {
        this.termsPoNo = termsPoNo;
    }
}
