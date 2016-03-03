package ch.swissbytes.fqmes.boundary.projectUser;

import ch.swissbytes.Service.business.project.ProjectService;
import ch.swissbytes.Service.business.projectUser.ProjectUserService;
import ch.swissbytes.domain.model.entities.ProjectEntity;

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
 * Created by Christian on 02/03/2016.
 */
@Named
@ViewScoped
public class ProjectUserBean implements Serializable {

    private static final Logger log = Logger.getLogger(ProjectUserBean.class.getName());

    @Inject
    private ProjectUserService service;

    @Inject
    private ProjectService projectService;

    private List<ProjectEntity> projectList;

    private List<ProjectEntity> projectAssignList;

    private List<ProjectEntity> selectedProjectList;

    private Long userId;

    private String searchTerm;

    @PostConstruct
    public void init() {
        log.info("create ProjectUserBean");
        projectList = new ArrayList<>();
        selectedProjectList = new ArrayList<>();
        projectAssignList = new ArrayList<>();
        load();
    }

    private void load() {
        projectList.addAll(projectService.getAllProjects());
    }

    @PreDestroy
    public void destroy() {
        log.info("destroy ProjectUserBean");
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public List<ProjectEntity> getProjectList() {
        return projectList;
    }

    public void setProjectList(List<ProjectEntity> projectList) {
        this.projectList = projectList;
    }

    public List<ProjectEntity> getSelectedProjectList() {
        return selectedProjectList;
    }

    public void setSelectedProjectList(List<ProjectEntity> selectedProjectList) {
        this.selectedProjectList = selectedProjectList;
    }

    public List<ProjectEntity> getProjectAssignList() {
        return projectAssignList;
    }

    public void setProjectAssignList(List<ProjectEntity> projectAssignList) {
        this.projectAssignList = projectAssignList;
    }
}
