package ch.swissbytes.procurement.boundary.project;

import ch.swissbytes.Service.business.enumService.EnumService;
import ch.swissbytes.Service.business.logo.LogoService;
import ch.swissbytes.Service.business.moduleGrantedAccess.ModuleGrantedAccessService;
import ch.swissbytes.Service.business.project.ProjectService;
import ch.swissbytes.Service.business.role.RoleDao;
import ch.swissbytes.Service.business.supplierProc.SupplierProcService;
import ch.swissbytes.Service.business.user.UserService;
import ch.swissbytes.Service.business.userRole.UserRoleService;
import ch.swissbytes.domain.model.entities.*;
import ch.swissbytes.domain.types.ModuleSystemEnum;
import ch.swissbytes.domain.types.RoleEnum;
import ch.swissbytes.domain.types.StatusEnum;
import ch.swissbytes.fqmes.util.Encode;
import ch.swissbytes.procurement.boundary.logo.LogoBean;
import ch.swissbytes.procurement.boundary.menu.MainMenuBean;
import org.apache.commons.lang.StringUtils;
import org.omnifaces.util.Messages;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by Christian on 15/05/2015.
 */
@Named
@ViewScoped
public class ProjectBean implements Serializable {

    public static final Logger log = Logger.getLogger(ProjectBean.class.getName());

    @Inject
    private MainMenuBean mainMenuBean;

    @Inject
    private ProjectService projectService;

    @Inject
    private SupplierProcService supplierProcService;

    @Inject
    private LogoService logoService;

    private List<SupplierProcEntity> supplierProcList;

    private List<LogoEntity> logoList;

    private List<ProjectCurrencyEntity> projectCurrencyList;

    private ProjectEntity projectEntity;

    private ProjectCurrencyEntity projectCurrencyEntity;

    private boolean isCreateProject;

    private Long projectId;


    @PostConstruct
    public void init (){
        log.info("ProjectBean created");
        projectEntity = new ProjectEntity();
        projectCurrencyEntity = new ProjectCurrencyEntity();
        supplierProcList = new ArrayList<>();
        projectCurrencyList = new ArrayList<>();
        logoList = new ArrayList<>();
        loadSupplierProcList();
        loadLogoList();
    }

    @PreDestroy
    public void destroy(){
        log.info("ProjectBean destroying");
    }

    public void loadActionCrud(){
        log.info("load action crud");
        if(isCreateProject){

        }else{
            projectEntity = projectService.findProjectById(projectId);
        }
    }

    public String doSave(){
        log.info("do save");
        if(dataValidate()) {
            projectService.doSave(projectEntity);
            return "list?faces-redirect=true";
        }
        mainMenuBean.select(0);
        return "";
    }

    public String doUpdate(){
        log.info("do update");
        if(dataValidateToUpdate()) {
            projectService.doUpdate(projectEntity);
            return "list?faces-redirect=true";
        }
        mainMenuBean.select(0);
        return "";
    }

    public void addCurrency(){
        log.info("add currency");
        projectCurrencyList.add(projectCurrencyEntity);
        projectCurrencyEntity = new ProjectCurrencyEntity();
    }

    private void loadSupplierProcList(){
        supplierProcList = supplierProcService.findAll();
    }

    private void loadLogoList(){
        logoList = logoService.findAll();
    }

    private boolean dataValidate() {
        boolean result = true;
        if(projectService.existsProjectNumber(projectEntity.getProjectNumber())){
            Messages.addFlashError("projectNumber", "Project number was already registered!");
            result = false;
        }
        return result;
    }

    private boolean dataValidateToUpdate() {
        boolean result = true;
        if(projectService.validateDuplicityProjectNumber(projectEntity.getProjectNumber(), projectEntity.getId())){
            Messages.addError("projectNumber", "Project number was already registered!");
            result = false;
        }
        return result;
    }

    public ProjectEntity getProjectEntity() {
        return projectEntity;
    }

    public void setProjectEntity(ProjectEntity projectEntity) {
        this.projectEntity = projectEntity;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public boolean isCreateProject() {
        return isCreateProject;
    }

    public void setCreateProject(boolean isCreateProject) {
        this.isCreateProject = isCreateProject;
    }

    public List<SupplierProcEntity> getSupplierProcList() {
        return supplierProcList;
    }

    public List<LogoEntity> getLogoList() {
        return logoList;
    }

    public List<ProjectCurrencyEntity> getProjectCurrencyList() {
        return projectCurrencyList;
    }

    public ProjectCurrencyEntity getProjectCurrencyEntity() {
        return projectCurrencyEntity;
    }

    public void setProjectCurrencyEntity(ProjectCurrencyEntity projectCurrencyEntity) {
        this.projectCurrencyEntity = projectCurrencyEntity;
    }



    @Inject
    private LogoBean logoBean;


}
