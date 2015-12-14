package ch.swissbytes.fqmes.boundary.dashboard;

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
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/**
 * Created by Christian on 11/12/2015.
 */
@Named
@ViewScoped
public class DashboardBean implements Serializable {

    private static final Logger log = Logger.getLogger(DashboardBean.class.getName());

    @Inject
    private ProjectService projectService;

    @Inject
    private PurchaseOrderService poService;

    private ProjectEntity projectSelected;

    private List<ProjectEntity> projectList;

    private String totalOfPOs;

    ResourceBundle bundle = ResourceBundle.getBundle("messages_en");


    @PostConstruct
    public void init(){
        log.info("Created DashboardBean");
        projectList = projectService.findAllProjects();
        loadDataOfDashboard();
    }

    @PreDestroy
    public void destroy(){

    }

    private void loadDataOfDashboard() {
        totalOfPOs = String.valueOf(poService.getTotalNumberOfPOs(projectSelected!=null?projectSelected.getId():-1));
    }

    public void refreshDataOfDashboard(){
        loadDataOfDashboard();
    }

    public String getTitleDashboard(){
        String bundleStr = bundle.getString("dashboard.main.title");
        String projectStr = projectSelected!=null?projectSelected.getProjectNumber():"";
        return bundleStr +" "+ projectStr ;
    }

    public ProjectEntity getProjectSelected() {
        return projectSelected;
    }

    public void setProjectSelected(ProjectEntity projectSelected) {
        this.projectSelected = projectSelected;
    }

    public List<ProjectEntity> getProjectList() {
        return projectList;
    }

    public String getTotalOfPOs() {
        return totalOfPOs;
    }

    public void setTotalOfPOs(String totalOfPOs) {
        this.totalOfPOs = totalOfPOs;
    }
}
