package ch.swissbytes.fqmes.boundary.projectUser;

import ch.swissbytes.Service.business.project.ProjectService;
import ch.swissbytes.Service.business.projectUser.ProjectUserService;
import ch.swissbytes.Service.business.user.UserService;
import ch.swissbytes.domain.model.entities.ProjectEntity;
import ch.swissbytes.domain.model.entities.ProjectTextSnippetEntity;
import ch.swissbytes.domain.model.entities.ProjectUserEntity;
import ch.swissbytes.domain.model.entities.UserEntity;
import ch.swissbytes.domain.types.ModuleSystemEnum;
import ch.swissbytes.domain.types.StatusEnum;
import ch.swissbytes.fqm.boundary.UserSession;
import ch.swissbytes.fqmes.util.Encode;
import org.omnifaces.util.Messages;
import org.picketlink.credential.DefaultLoginCredentials;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
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

    @Inject
    private DefaultLoginCredentials credentials;

    @Inject
    private UserService userService;

    @Inject
    private UserSession userSession;

    private List<ProjectEntity> projectList;

    private List<ProjectUserEntity> projectAssignList;

    private List<ProjectEntity> selectedProjectList;

    private List<ProjectUserEntity> selectedProjectUserList;

    private UserEntity userSelected;

    private Long userId;

    private String searchTerm;

    private Long tempProjectUserId = -1L;

    @PostConstruct
    public void init() {
        log.info("create ProjectUserBean");
        initLists();
    }

    private void initLists(){
        searchTerm = "";
        projectList = new ArrayList<>();
        selectedProjectList = new ArrayList<>();
        projectAssignList = new ArrayList<>();
        selectedProjectUserList = new ArrayList<>();
        userSelected = new UserEntity();
    }

    @PreDestroy
    public void destroy() {
        log.info("destroy ProjectUserBean");
    }

    public void load(final Long userId) {
        initLists();
        projectList.addAll(projectService.getProjectsAssignables());
        projectAssignList.addAll(service.findByUserId(userId));
        List<ProjectEntity> auxList = new ArrayList<>();
        userSelected = userService.findUserById(userId);
        for(ProjectUserEntity p : projectAssignList){
            for(ProjectEntity pr : projectList){
                if(p.getProject().getId().longValue() == pr.getId().longValue()){
                    auxList.add(p.getProject());
                }
            }
        }
        projectList.removeAll(auxList);
    }

    public void addToProjectUserList(){
        for(ProjectEntity p : selectedProjectList){
            ProjectUserEntity entity = new ProjectUserEntity();
            entity.setId(tempProjectUserId);
            tempProjectUserId--;
            entity.setStatus(StatusEnum.ENABLE);
            entity.setCreated(new Date());
            entity.setLastUpdate(new Date());
            entity.setUserCreated(userSession.getCurrentUser());
            entity.setUserLastUpdate(userSession.getCurrentUser());
            entity.setProject(p);
            entity.setUser(userSelected);
            entity.setModuleSystem(ModuleSystemEnum.EXPEDITING);
            projectAssignList.add(entity);
        }
        projectList.removeAll(selectedProjectList);
        selectedProjectList.clear();
    }

    public void removeFromProjectUserList(){
        for(ProjectUserEntity p : selectedProjectUserList){
            if(p.getId()<0){
                projectAssignList.remove(p);
            }else{
                for(ProjectUserEntity pu : projectAssignList){
                    if(p.getId().longValue() == pu.getId().longValue()){
                        pu.setStatus(StatusEnum.DELETED);
                    }
                }
            }
            projectList.add(projectService.findProjectById(p.getProject().getId()));
        }
        selectedProjectUserList.clear();
    }

    public List<ProjectUserEntity> filteredList() {
        List<ProjectUserEntity> list = new ArrayList<>();
        for (ProjectUserEntity p : projectAssignList) {
            if (p.getStatus() != null && p.getStatus().getId().intValue() == StatusEnum.ENABLE.getId().intValue()) {
                list.add(p);
            }
        }
        return list;
    }

    public void doSave(){
        for(ProjectUserEntity p : projectAssignList){
            if(p.getId() < 0) {
                p.setId(null);
                service.doSave(p);
            }else{
                service.doUpdate(p);
            }

        }
        RequestContext context = RequestContext.getCurrentInstance();
        RequestContext.getCurrentInstance().update("pickSystemFormId");
        context.execute("PF('projectUserModal').hide();");
        Messages.addFlashGlobalInfo("Project permissions has been updated!");
    }

    public void searchProject(){
        List<Long> ids = new ArrayList<>();
        for(ProjectUserEntity pu : projectAssignList){
            if(hasProjectUserStatusEnable(pu)){
                ids.add(pu.getProject().getId());
            }
        }
        List<ProjectEntity> list = new ArrayList<>();
        if(!ids.isEmpty()){
            projectList.clear();
            list = projectService.findListByProjectNumber(searchTerm,ids);
        }
        projectList.addAll(list);
    }

    private boolean hasProjectUserStatusEnable(ProjectUserEntity entity){
        return entity.getStatus().getId().intValue() == StatusEnum.ENABLE.getId().intValue();
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

    public List<ProjectEntity> getSelectedProjectList() {
        return selectedProjectList;
    }

    public void setSelectedProjectList(List<ProjectEntity> selectedProjectList) {
        this.selectedProjectList = selectedProjectList;
    }

    public List<ProjectUserEntity> getProjectAssignList() {
        return projectAssignList;
    }


    public List<ProjectUserEntity> getSelectedProjectUserList() {
        return selectedProjectUserList;
    }

    public void setSelectedProjectUserList(List<ProjectUserEntity> selectedProjectUserList) {
        this.selectedProjectUserList = selectedProjectUserList;
    }

    public Long getTempProjectUserId() {
        return tempProjectUserId;
    }

    public void setTempProjectUserId(Long tempProjectUserId) {
        this.tempProjectUserId = tempProjectUserId;
    }

    public UserEntity getUserSelected() {
        return userSelected;
    }

    public void setUserSelected(UserEntity userSelected) {
        this.userSelected = userSelected;
    }
}
