package ch.swissbytes.procurement.boundary.report.expediting;

import ch.swissbytes.Service.business.deliverable.DeliverableDao;
import ch.swissbytes.Service.business.project.ProjectService;
import ch.swissbytes.Service.business.purchase.PurchaseOrderService;
import ch.swissbytes.domain.model.entities.DeliverableEntity;
import ch.swissbytes.domain.model.entities.ProjectEntity;
import ch.swissbytes.domain.model.entities.PurchaseOrderEntity;
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

    private List<ProjectEntity> projectList;

    private List<PurchaseOrderEntity> purchaseOrderList;

    private ProjectEntity selectedProject;

    private String termsPoNo;


    private List<DeliverableDto> deliverableDtoList;

    @PostConstruct
    public void create() {
        log.info("creating reportDeliverableBean");
        selectedProject = new ProjectEntity();
        projectList = new ArrayList<>();
        purchaseOrderList = new ArrayList<>();
        deliverableDtoList = new ArrayList<>();
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
            loadDeliverablesDtoList(selectedProject.getId(), null);
        } else {
            purchaseOrderList.clear();
            deliverableDtoList.clear();
        }
    }

    public void filterDeliverableDtoListByPoNo() {
        log.info("filtering by project id and poNo");
        loadDeliverablesDtoList(selectedProject.getId(), termsPoNo != null ? termsPoNo : "");

    }

    public void printReportDeliverables() {
        reportProcBean.printReportDeliverables(deliverableDtoList, purchaseOrderList.get(0), selectedProject.getId(), termsPoNo);
    }

    private void loadDeliverablesDtoList(Long projectId, String poNo) {
        purchaseOrderList.clear();
        purchaseOrderList = purchaseOrderService.purchaseListByProjectIdAnPoNo(projectId, poNo);
        for (PurchaseOrderEntity p : purchaseOrderList) {
            p.getPoEntity().getDeliverables().addAll(deliverableDao.findDeliverableByPurchaseOrder(p.getPoEntity().getId()));
        }
        deliverableDtoList.clear();
        for (PurchaseOrderEntity p : purchaseOrderList) {
            for (DeliverableEntity d : p.getPoEntity().getDeliverables()) {
                DeliverableDto dto = new DeliverableDto(p, d);
                deliverableDtoList.add(dto);
            }
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

    public List<DeliverableDto> getDeliverableDtoList() {
        return deliverableDtoList;
    }

    public String getTermsPoNo() {
        return termsPoNo;
    }

    public void setTermsPoNo(String termsPoNo) {
        this.termsPoNo = termsPoNo;
    }
}
