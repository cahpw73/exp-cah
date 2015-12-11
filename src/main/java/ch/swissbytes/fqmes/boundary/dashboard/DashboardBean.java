package ch.swissbytes.fqmes.boundary.dashboard;

import ch.swissbytes.Service.business.project.ProjectService;
import ch.swissbytes.domain.model.entities.ProjectEntity;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;
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

    private ProjectEntity projectSelected;

    private List<ProjectEntity> projectList;


    @PostConstruct
    public void init(){
        log.info("Created DashboardBean");
        projectList = projectService.findAllProjects();
    }

    @PreDestroy
    public void destroy(){

    }

    public String getTitleDashboard(){
        return "";
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
}
