package ch.swissbytes.Service.business.project;

import ch.swissbytes.Service.business.moduleGrantedAccess.ModuleGrantedAccessService;
import ch.swissbytes.Service.business.projectCurrency.ProjectCurrencyService;
import ch.swissbytes.Service.business.projectTextSnippet.ProjectTextSnippetService;
import ch.swissbytes.Service.business.textSnippet.TextSnippetService;
import ch.swissbytes.Service.business.user.UserDao;
import ch.swissbytes.Service.business.userRole.UserRoleService;
import ch.swissbytes.domain.model.entities.*;
import ch.swissbytes.domain.types.StatusEnum;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by christian on 12/09/14.
 */
public class ProjectService implements Serializable {

    private static final Logger log = Logger.getLogger(ProjectService.class.getName());

    @Inject
    private ProjectDao projectDao;

    @Inject
    private ProjectCurrencyService projectCurrencyService;

    @Inject
    private ProjectTextSnippetService projectTextSnippetService;



    @Transactional
    public void doSave(ProjectEntity entity,List<ProjectCurrencyEntity> projectCurrencyList,List<ProjectTextSnippetEntity> projectTextSnippetList){
        if(entity != null){
            entity.setStatus(StatusEnum.ENABLE);
            entity.setLastUpdate(new Date());
            projectDao.doSave(entity);
            for(ProjectCurrencyEntity pc : projectCurrencyList){
                if(pc.getId() < 0){
                    pc.setId(null);
                }
                pc.setProject(entity);
                projectCurrencyService.doSave(pc);
            }
            for(ProjectTextSnippetEntity pt : projectTextSnippetList){
                if(pt.getId()==null){

                }
                pt.setProject(entity);
                projectTextSnippetService.doSave(pt);
            }
        }
    }

    @Transactional
    public void doUpdate(ProjectEntity entity,List<ProjectCurrencyEntity> projectCurrencyList,List<ProjectTextSnippetEntity> projectTextSnippetList){
        if(entity != null){
            entity.setLastUpdate(new Date());
            projectDao.doUpdate(entity);
            for(ProjectCurrencyEntity pc : projectCurrencyList){
                if(pc.getId() < 0){
                    pc.setId(null);
                    pc.setProject(entity);
                }
                projectCurrencyService.doUpdate(pc);
            }
            for(ProjectTextSnippetEntity pt : projectTextSnippetList){
                if(pt.getId() == null){
                    pt.setProject(entity);
                    pt.setStatus(StatusEnum.ENABLE);
                }
                pt.setLastUpdate(new Date());
                projectTextSnippetService.doUpdate(pt);
            }
        }
    }


    public ProjectEntity findProjectById(Long projectId) {
        List<ProjectEntity> list = projectDao.findById(ProjectEntity.class,projectId);
        ProjectEntity entity = null;
        if(!list.isEmpty()){
            entity = list.get(0);
        }
        return entity;
    }

    public List<ProjectEntity> findAllProjects() {
        return projectDao.getProjectList();
    }

    public List<ProjectEntity> doSearch(final String searchTerm) {
        return projectDao.findBySearchTerm(searchTerm);
    }

    public ProjectEntity findByProjectNumber(String projectNumber) {
        List<ProjectEntity> list = projectDao.findByProjectNumber(projectNumber) ;
        ProjectEntity entity = null;
        if(!list.isEmpty()){
            entity = list.get(0);
        }
        return entity;
    }

    public boolean existsProjectNumber(String projectNumber) {
        ProjectEntity project = findByProjectNumber(projectNumber);
        return project != null ? true : false;
    }

    public boolean validateDuplicityProjectNumber(final String projectNumber,final Long id) {
        List<ProjectEntity> list = projectDao.findDuplicityProjectNumber(projectNumber,id);
        boolean result = false;
        if(!list.isEmpty()){
            result = true;
        }
        return result;
    }

    public List<ProjectCurrencyEntity> findProjectCurrencyByProjectId(final Long id){
        return projectCurrencyService.findByProjectId(id);
    }

    public List<ProjectTextSnippetEntity> findProjectTextSnippetByProjectId(Long projectId) {
        return projectTextSnippetService.findByProjectId(projectId);
    }
}
