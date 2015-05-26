package ch.swissbytes.procurement.boundary.project;

import ch.swissbytes.Service.business.enumService.EnumService;
import ch.swissbytes.Service.business.moduleGrantedAccess.ModuleGrantedAccessService;
import ch.swissbytes.Service.business.project.ProjectService;
import ch.swissbytes.Service.business.user.UserService;
import ch.swissbytes.Service.business.userRole.UserRoleService;
import ch.swissbytes.domain.model.entities.*;
import ch.swissbytes.domain.types.ModuleSystemEnum;
import ch.swissbytes.domain.types.RoleEnum;
import ch.swissbytes.domain.types.StatusEnum;
import ch.swissbytes.fqmes.boundary.user.VerificationTokenBean;

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
 * Created by Christian on 25/05/2015.
 */
@Named
@ViewScoped
public class ProjectsBean implements Serializable {

    public static final Logger log = Logger.getLogger(ProjectsBean.class.getName());

    @Inject
    private ProjectService projectService;

    private List<ProjectEntity> projectList;


    private String searchTerm;

    @PostConstruct
    public void init (){
        log.info("ProjectsBean was created");
        projectList = new ArrayList<>();
        loadProjects();
    }

    @PreDestroy
    public void destroy(){
        log.info("ProjectsBean destroying");
    }

    public void loadProjects(){
        projectList = projectService.findAllProjects();
    }

    public void doSearch(){
        projectList.clear();
        projectList = projectService.doSearch(searchTerm);
    }

    public void doClean(){
        projectList.clear();
        loadProjects();
        searchTerm = "";
    }

    public List<ProjectEntity> getProjectList() {
        return projectList;
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

}
