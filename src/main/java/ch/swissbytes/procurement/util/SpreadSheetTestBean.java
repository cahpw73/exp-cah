package ch.swissbytes.procurement.util;

import ch.swissbytes.Service.business.Spreadsheet.SpreadsheetService;
import ch.swissbytes.Service.business.project.ProjectService;
import ch.swissbytes.Service.business.purchase.PurchaseOrderService;
import ch.swissbytes.domain.model.entities.ProjectEntity;
import ch.swissbytes.domain.model.entities.PurchaseOrderEntity;

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
 * Created by Christian on 21/07/2015.
 */
@Named
@ViewScoped
public class SpreadSheetTestBean implements Serializable {

    public static final Logger log = Logger.getLogger(SpreadSheetTestBean.class.getName());

    @Inject
    private PurchaseOrderService poService;
    @Inject
    private ProjectService projectService;
    @Inject
    private SpreadsheetService spreadsheetService;

    private List<ProjectEntity> projectList;
    private List<PurchaseOrderEntity> purchaseOrderList;

    private ProjectEntity selectedProject;

    private String nameFile;

    @PostConstruct
    public void create() {
        log.info("creating SpreadSheetTestBean");
        projectList = new ArrayList<>();
        purchaseOrderList = new ArrayList<>();
        loadProjects();
    }

    @PreDestroy
    public void destroy() {
        log.info("destroyed SpreadSheetTestBean");
    }

    public void loadProjects() {
        log.info("loading projects");
        projectList = projectService.findAllProjects();
    }

    public void loadPOS(){
        log.info("load purchase orders");
        log.info("Project Id: " + selectedProject.getId());
        purchaseOrderList = poService.purchaseListByProject(selectedProject.getId());
    }

    public void generateWorkbook(){
        spreadsheetService.generatorWorkbook(purchaseOrderList,nameFile);
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

    public void setProjectList(List<ProjectEntity> projectList) {
        this.projectList = projectList;
    }

    public String getNameFile() {
        return nameFile;
    }

    public void setNameFile(String nameFile) {
        this.nameFile = nameFile;
    }
}
